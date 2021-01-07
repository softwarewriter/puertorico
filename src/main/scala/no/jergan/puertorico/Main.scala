package no.jergan.puertorico

import cats.effect.{ExitCode, IO, IOApp, Resource}
import no.jergan.puertorico.model.{Frame, Frames, Move, Player, World}
import org.http4s.server.Server

/**
 * Entry point.
 *
 * @author <a href="mailto:oyvind@jergan.no">Oyvind Jergan</a>
 */
object Main extends IOApp {

  def createApplication(configuration: Configuration): Resource[IO, Server[IO]] = {
    for {
      executionContext <- ExecutionContexts.cpuBoundExecutionContext[IO]("main-execution-context")
      httpServer <- Endpoints.create[IO](configuration, executionContext, new Frames(World.initial(configuration.players)))
    } yield httpServer
  }

  override def run(args: List[String]): IO[ExitCode] = {
    createApplication(Configuration(8080, "0.0.0.0", List("Ada", "Erle", "Inez")))
      .use(_ => IO.never)
  }

}
