package codionics

import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest._
import org.scalatest.matchers.should
import org.scalatest.freespec.AsyncFreeSpec
import codionics.db.SessionResource
import com.datastax.oss.driver.api.core.CqlSession

abstract class BaseSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with should.Matchers
    with OptionValues
    with EitherValues
    with BeforeAndAfterAll {

  protected var _database: Option[(CqlSession, IO[Unit])] = None
  protected def database: CqlSession                      = _database.getOrElse(sys.error("not currently alive!"))._1

  override def beforeAll(): Unit = {
    println("getting session")
    // _database = Some(SessionResource.cqlSessionResource.allocated.unsafeRunSync())
    // if (_database != null)
    //   println("_database is not null")
    ()
  }

  override def afterAll(): Unit = {
    // _database.foreach(_._2.unsafeRunSync())
    println("closing session")
    _database = None
  }
}
