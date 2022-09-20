package codionics.db

import weaver._
import cats.effect._
import com.datastax.oss.driver.api.core.CqlSession
import codionics.domain._

class PlayerTypedRepositorySpec(global: GlobalRead) extends IOSuite {
  override type Res = CqlSession

  override def sharedResource: Resource[IO, Res] = global.getOrFailR[CqlSession]()

  val city          = "Ajmer"
  val nicknamePK    = "fedex"
  val newNicknamePK = "chintu1"
  val updatedCity   = "Jodhpur2"

  val playerRepo = new PlayerTypedRepository()

  test("PlayerTypedRepository getAll method") { (session, log) =>
    for {
      _       <- log.info(s"test getAll")
      players <- playerRepo.getAll(session)
      _       <- log.info(s"Total rows: ${players.size}")
    } yield expect(players.size == 6)
  }

  test("PlayerTypedRepository getByPK method") { (session, log) =>
    for {
      _      <- log.info(s"test getByPK")
      player <- playerRepo.getByPK(nicknamePK)(session)
      _      <- log.info(s"Player by PK: $player")
    } yield expect(player.getOrElse(Player.NULL_OBJECT).nickName == nicknamePK)
  }

  test("PlayerTypedRepository getCount method") { (session, log) =>
    for {
      _     <- log.info(s"test getCount")
      count <- playerRepo.getCount(session)
      _     <- log.info(s"Players count (in the table): $count")
    } yield expect(count == 6)
  }

  test("PlayerTypedRepository getByQuery method") { (session, log) =>
    val query = s"select * from players where city = '${city}'"

    for {
      _       <- log.info(s"test getByQuery")
      players <- playerRepo.getByQuery(query)(session)
      _       <- log.info(s"Rows by query: ${players.size}")
      player   = players.head
    } yield expect(player.city == city)
  }

  test("PlayerTypedRepository insert method") { (session, log) =>
    val data = Player("Rishi1", "Kapoor1", newNicknamePK, "Jodhpur1", "India1", "305002")

    for {
      _        <- log.info(s"test insert")
      exists   <- playerRepo.getByPK(newNicknamePK)(session)
      inserted <- playerRepo.insert(data)(session)
      _        <- log.info(s"Inserted row: $inserted")
    } yield expect(inserted.getOrElse(Player.NULL_OBJECT).city == city)
  }

  test("PlayerTypedRepository update method") { (session, log) =>
    val data = Player("Rishi2", "Kapoor2", newNicknamePK, updatedCity, "India2", "305003")

    for {
      _       <- log.info(s"test update")
      updated <- playerRepo.update(data)(session)
      _       <- log.info(s"Updated row: $updated")
    } yield expect(updated.getOrElse(Player.NULL_OBJECT).city == updatedCity)
  }

  test("PlayerTypedRepository delete method") { (session, log) =>
    val nickname = "chintu1"

    for {
      _           <- log.info(s"test delete")
      countBefore <- playerRepo.getCount(session)
      _           <- playerRepo.delete(nickname)(session)
      countAfter  <- playerRepo.getCount(session)
      _           <- log.info(s"Count before: $countBefore, after: $countAfter")
    } yield expect(countBefore == countAfter + 1)
  }
}
