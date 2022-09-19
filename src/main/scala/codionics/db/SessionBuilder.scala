package codionics.db

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlIdentifier
import cats.data.Kleisli
import cats.effect.{IO, Ref}
import codionics.config.Config
import java.net.InetSocketAddress

object SessionBuilder {
  val refSession: Ref[IO, Option[CqlSession]] = Ref.unsafe[IO, Option[CqlSession]](None)

  /** Creates a new Casandra session if it does not exists, else returns the already created one.
    */
  def getSession(): Kleisli[IO, Config, Ref[IO, Option[CqlSession]]] = {
    Kleisli { config =>
      for {
        sessionExists <- sessionIsDefined
        session       <- if (sessionExists) {
                           println("Session already exists, so use the existing one")
                           IO(refSession)
                         } else {
                           println(s"Session does not exist, so create a new one")
                           createSession().run(config)
                         }
      } yield session
    }
  }

  /** Closes the only Cassandra session.
    */
  def cleanupSession() = {
    for {
      session <- refSession.get
      _        = session.map(_.close())
      _       <- refSession.set(None)
    } yield ()
  }

  private def createSession(): Kleisli[IO, Config, Ref[IO, Option[CqlSession]]] = {
    Kleisli { config =>
      val session = CqlSession
        .builder()
        .addContactPoint(new InetSocketAddress(config.host, config.port))
        .withLocalDatacenter(config.datacenter)
        .withAuthCredentials(config.username, config.password)
        .withKeyspace(CqlIdentifier.fromCql(config.keyspace))
        .build()

      for {
        _ <- refSession.set(Some(session))
      } yield refSession
    }
  }

  private def sessionIsDefined: IO[Boolean] = {
    for {
      session <- refSession.get
    } yield session.isDefined
  }
}
