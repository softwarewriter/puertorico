package no.jergan.puertorico

import cats.effect.{ExitCode, IO, IOApp, Resource}
import no.jergan.puertorico.model.{Frame, Move, Player, World}
import org.http4s.server.Server

import scala.collection.mutable.ListBuffer

/**
 * Entry point.
 *
 * @author <a href="mailto:oyvind@jergan.no">Oyvind Jergan</a>
 */
object Main extends IOApp {

  def createApplication(configuration: Configuration): Resource[IO, Server[IO]] = {
    for {
      executionContext <- ExecutionContexts.cpuBoundExecutionContext[IO]("main-execution-context")
      httpServer <- Endpoints.create[IO](configuration, executionContext, new ListBuffer().addOne(Frame(None, None, World.initial(configuration.players))))
    } yield httpServer
  }

  override def run(args: List[String]): IO[ExitCode] = {
    createApplication(Configuration(8080, "0.0.0.0", List("Ole", "Dole", "Doff")))
      .use(_ => IO.never)
  }

}
