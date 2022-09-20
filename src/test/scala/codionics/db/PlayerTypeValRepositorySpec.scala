package codionics.db

import weaver._
import cats.effect._
import com.datastax.oss.driver.api.core.CqlSession
import codionics.domain.TypeVal

class PlayerTypeValRepositorySpec(global: GlobalRead) extends IOSuite {
  override type Res = CqlSession

  override def sharedResource: Resource[IO, Res] = global.getOrFailR[CqlSession]()

  val city          = "Ajmer"
  val nicknamePK    = "fedex"
  val newNicknamePK = "chintu1"
  val updatedCity   = "Jodhpur2"

  val playerRepo = new PlayerRepository()

  test("PlayerRepository getAll method") { (session, log) =>
    for {
      _       <- log.info(s"test getAll")
      players <- playerRepo.getAll(session)
      _       <- log.info(s"Total rows: ${players.size}")
    } yield expect(players.size == 6)
  }

  test("PlayerRepository getByPK method") { (session, log) =>
    for {
      _      <- log.info(s"test getByPK")
      player <- playerRepo.getByPK(nicknamePK)(session)
      _      <- log.info(s"Player by PK: $player")
    } yield expect(player.get("nickname").toValueString == nicknamePK)
  }

  test("PlayerRepository getCount method") { (session, log) =>
    for {
      _     <- log.info(s"test getCount")
      count <- playerRepo.getCount(session)
      _     <- log.info(s"Players count (in the table): $count")
    } yield expect(count == 6)
  }

  test("PlayerRepository getByQuery method") { (session, log) =>
    val query = s"select * from players where city = '${city}'"

    for {
      _       <- log.info(s"test getByQuery")
      players <- playerRepo.getByQuery(query)(session)
      _       <- log.info(s"Rows by query: ${players.size}")
      player   = players.head
    } yield expect(player.getOrElse("city", TypeVal.DEFAULT_STRING).toValueString == city)
  }

  test("PlayerRepository insert method") { (session, log) =>
    val data = Map(
      "nickname"   -> newNicknamePK,
      "first_name" -> "Rishi1",
      "last_name"  -> "Kapoor1",
      "city"       -> "Jodhpur1",
      "country"    -> "India1",
      "zip"        -> "305002"
    )

    for {
      _        <- log.info(s"test insert")
      exists   <- playerRepo.getByPK(newNicknamePK)(session)
      inserted <- playerRepo.insert(data)(session)
      _        <- log.info(s"Inserted row: $inserted")
    } yield expect(inserted.get("nickname").toValueString == city)
  }

  test("PlayerRepository update method") { (session, log) =>
    val data = Map(
      "nickname"   -> newNicknamePK,
      "first_name" -> "Rishi2",
      "last_name"  -> "Kapoor2",
      "city"       -> updatedCity,
      "country"    -> "India2",
      "zip"        -> "305003"
    )

    for {
      _       <- log.info(s"test update")
      updated <- playerRepo.update(data)(session)
      _       <- log.info(s"Updated row: $updated")
    } yield expect(updated.get("city").toValueString == updatedCity)
  }

  test("PlayerRepository delete method") { (session, log) =>
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
