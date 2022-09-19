package codionics.db

import weaver._
import cats.effect._
import com.datastax.oss.driver.api.core.CqlSession

object PlayerRepoSpec extends IOSuite {
  override type Res = CqlSession

  override def sharedResource: Resource[IO, Res] = SessionResource.cqlSessionResource

  val playerRepo = new PlayerRepository()

  test("PlayerRepository getAll method") { session =>
    for {
      players <- playerRepo.getAll(session)
    } yield expect(players.size == 6)
  }
}
