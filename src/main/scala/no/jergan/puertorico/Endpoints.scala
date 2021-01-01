package no.jergan.puertorico

import cats.effect.{ConcurrentEffect, Resource, Sync, Timer}
import io.unsecurity.Server.toHttpRoutes
import org.http4s.dsl.io.{->, GET, Root}
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, Response, Status}

import scala.concurrent.ExecutionContext

/**
 * HTTP endpoints definitions.
 *
 * @author <a href="mailto:oyvind@jergan.no">Oyvind Jergan</a>
 */
object Endpoints {

   def create[F[_]: ConcurrentEffect: Timer](configuration: Configuration, executionContext: ExecutionContext): Resource[F, Server[F]] = {

      val simpleHttpService: HttpRoutes[F] = HttpRoutes.of[F] {
         case GET -> Root => Sync[F].pure {
            Response[F](Status.Ok).withEntity("i am simple")
         }
      }
      val routes = Router(
         "/simple"-> simpleHttpService,
      )

      HttpServer[F](configuration.port, configuration.bindAddress, executionContext, routes)
   }

}
