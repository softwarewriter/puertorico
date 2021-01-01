package no.jergan.puertorico

import cats.data.OptionT
import cats.effect.{ConcurrentEffect, Resource, Sync, Timer}
import cats.implicits._
import io.unsecurity.HttpProblem
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpRoutes, Method, _}
import org.http4s.implicits._
import org.log4s.{Error, Info, LogLevel, getLogger}

import scala.concurrent.ExecutionContext

object HttpServer {

  private[this] val log = getLogger

  def apply[F[_]: ConcurrentEffect: Timer](port: Int, bindAddress: String, ec: ExecutionContext, routes: HttpRoutes[F]): Resource[F, server.Server[F]] = {
    BlazeServerBuilder[F](ec)
      .bindHttp(port, bindAddress)
      .enableHttp2(false)
      .withWebSockets(false)
      .withNio2(true)
      .withHttpApp(httpProblemMiddleware(routes).orNotFound)
      .resource
  }

  def httpProblemMiddleware[F[_]](service: HttpRoutes[F])(implicit F: Sync[F]): HttpRoutes[F] = HttpRoutes { req: Request[F] =>
    {
      def logged(response: Response[F], level: LogLevel) = {
        val loggedResp = response.withEntity(
          response.bodyText.evalTap(body => F.delay(log(level)(s"Error processing: ${req.pathInfo}, message: $body"))).through(fs2.text.utf8Encode)
        )
        response.contentType.fold(loggedResp)(ct => loggedResp.putHeaders(ct))
      }

      service
        .run(req)
        .map {
          case resp @ Status.ClientError(_) => logged(resp, Info)
          case resp @ Status.ServerError(_) => logged(resp, Error)
          case resp                         => resp
        }
    }.handleErrorWith { t =>
      OptionT.liftF(F.delay {
        val problem = HttpProblem.handleError(t)
        log.error(t)(s"Error processing [${req.pathInfo}] id [${problem.uuid}]")
        problem.toResponse[F]
      })
    }
  }

}
