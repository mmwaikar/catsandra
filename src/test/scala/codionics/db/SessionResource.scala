package codionics.db

import cats.effect._
import cats.effect.kernel.Resource
import codionics.config._
import codionics.db.SessionBuilder
import com.datastax.oss.driver.api.core.CqlSession
import weaver._

case class SessionHolder(cqlSessionResource: Resource[IO, CqlSession])

object SessionResource extends GlobalResource {

  // val xx: Resource[IO, String] = Resource.pure[IO, String]("hello world!")

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] =
    for {
      // foo <- Resource.pure[IO, String]("hello world!")
      _ <- global.putR(SessionHolder(cqlSessionResource))
      // _ <- global.putR(cqlSessionResource)
    } yield ()

  def cqlSessionResource: Resource[IO, CqlSession] = Resource.make(getSession)(_ => cleanupSession)

  def getSession: IO[CqlSession] = {
    for {
      config       <- new ConfigReaderImpl().load()
      sessionRef   <- SessionBuilder.getSession().run(config)
      maybeSession <- sessionRef.get
    } yield maybeSession.get
  }

  def cleanupSession: IO[Unit] = {
    for {
      _ <- IO(println("cleaning up session"))
      _ <- SessionBuilder.cleanupSession()
    } yield ()
  }
}
