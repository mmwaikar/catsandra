package codionics.db

import codionics.BaseSpec

import codionics.config._
import cats.effect.{IO, IOApp, Sync}
import scala.io.StdIn
import cats.effect.unsafe.implicits._
import cats.effect.ExitCode
import codionics.db.SessionBuilder
import cats.syntax.all._
import cats.effect.unsafe.implicits._
import codionics.db.PlayerRepository
import codionics.db.TypeValUtils._
import cats.effect.Ref
import com.datastax.oss.driver.api.core.CqlSession
import cats.effect.kernel.Resource

// class PlayerRepositorySpec extends BaseSpec {

//   // val playerRepo = new PlayerRepository()

//   // override def beforeAll(): Unit = {
//   //   // _database = Some(Database.resource.allocated.unsafeRunSync())
//   //   // ()
//   // }

//   // override def afterAll(): Unit = {
//   //   // _database.foreach(_._2.unsafeRunSync())
//   //   // _database = None
//   // }

//   "Given, a PlayerRepository" - {

//     "when the getAll method is called" - {

//       "should return all the rows of the players table" ignore {
//         // for {
//         //   players <- playerRepo.getAll(database)
//         //   _       <- players.size should be(2)
//         // } yield ()

//         // val players = playerRepo.getAll(database)
//         // players.size should be(2)
//         // players.head.name should be("Player 1")
//         // players.last.name should be("Player 2")
//         // 1 should be(1)

//         // playerRepo.getAll(database).asserting { players =>
//         //   players.size should be(2)
//         // }

//         IO(1).asserting(_ shouldBe 1)
//       }
//     }
//   }
// }
