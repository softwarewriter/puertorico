package no.jergan.puertorico

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}

import cats.effect.{Resource, Sync}

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object ExecutionContexts {
  private val log = org.log4s.getLogger

  def cpuBoundExecutionContext[F[_]: Sync](name: String): Resource[F, ExecutionContext] = {
    val bound = math.max(2, Runtime.getRuntime.availableProcessors())
    fixedThreadPool(name, bound)
  }

  def fixedThreadPool[F[_]](name: String, size: Int)(implicit F: Sync[F]): Resource[F, ExecutionContext] = {
    mkResource(F.delay(Executors.newFixedThreadPool(size, threadFactory(name))))
  }

  private def mkResource[F[_]: Sync](alloc: F[ExecutorService]): Resource[F, ExecutionContext] = {
    Resource.make(alloc)(es => shutdown(es)).map(executor => exitOnFatal(ExecutionContext.fromExecutor(executor)))
  }

  private def shutdown[F[_]: Sync](es: ExecutorService): F[Unit] = Sync[F].delay(es.shutdown())

  private def threadFactory(name: String): ThreadFactory = {
    new ThreadFactory {
      val ctr = new AtomicInteger(0)
      val group: ThreadGroup = {
        val s = System.getSecurityManager
        if (s != null)
          s.getThreadGroup
        else
          Thread.currentThread.getThreadGroup
      }

      def newThread(r: Runnable): Thread = {
        val t = new Thread(group, r, s"$name-${ctr.getAndIncrement()}", 0)
        t.setDaemon(false)
        if (t.getPriority != Thread.NORM_PRIORITY)
          t.setPriority(Thread.NORM_PRIORITY)
        t
      }
    }
  }

  private def exitOnFatal(ec: ExecutionContext): ExecutionContext = new ExecutionContext {
    def execute(r: Runnable): Unit = {
      ec.execute(() => {
        try {
          r.run()
        } catch {
          case NonFatal(t) =>
            reportFailure(t)
          case t: Throwable =>
            log.error(t)("Fatal error occurred. Terminating JVM.")
            Runtime.getRuntime.halt(1)
        }
      })
    }

    def reportFailure(t: Throwable): Unit =
      ec.reportFailure(t)
  }

}
