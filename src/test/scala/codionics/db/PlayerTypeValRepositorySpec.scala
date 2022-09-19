package codionics.db

import weaver._
import cats.effect._
import com.datastax.oss.driver.api.core.CqlSession

class PlayerTypeValRepositorySpec(global: GlobalRead) extends IOSuite {
  override type Res = CqlSession

  override def sharedResource: Resource[IO, Res] = global.getOrFailR[CqlSession]()

  val playerRepo = new PlayerRepository()

  test("PlayerRepository getAll method") { (session, log) =>
    for {
      players <- playerRepo.getAll(session)
      _       <- log.info(s"First player: ${players.head}")
    } yield expect(players.size == 6)
  }

  test("PlayerRepository getByPK method") { (session, log) =>
    val nickname = "fedex"

    for {
      player <- playerRepo.getByPK(nickname)(session)
      _      <- log.info(s"Player by PK: $player")
    } yield expect(player.get("nickname").toValueString == nickname)
  }
}
