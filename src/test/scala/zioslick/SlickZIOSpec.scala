package zioslick

import org.scalatest.{ FlatSpec, Matchers }
import slick.dbio.DBIO
import slick.jdbc.{ H2Profile, JdbcProfile }
import zio.{ DefaultRuntime, Runtime }
import zio.clock.Clock
import zio.internal.PlatformLive
import zio.duration._

import scala.concurrent.Future

class SlickZIOSpec extends FlatSpec with Matchers {
  implicit val profile: JdbcProfile = H2Profile

  val action: DBIO[Int] = {
    import profile.api._
    sql"select 1".as[Int].head
  }

  "all manual: simple select using zio" should "work" in {
    val db =
      profile.api.Database.forDriver(org.h2.Driver.load(), "jdbc:h2:mem:")

    val dbRuntime =
      Runtime(new SlickDatabase with Clock.Live { val database = db }, PlatformLive.Default)

    val testRuntime = Runtime(12, PlatformLive.Default)

    dbRuntime.unsafeRun(action) shouldBe 1

    val sumOfZIO: SlickZIO[Int] = for {
      a <- SlickZIO(action)
      b <- SlickZIO(action)
    } yield a + b

    dbRuntime.unsafeRun(sumOfZIO) shouldBe 2

    import scala.concurrent.ExecutionContext.Implicits.global

    val sumOfDBIO: DBIO[Int] = for {
      a <- action
      b <- action
    } yield a + b

    dbRuntime.unsafeRun(sumOfDBIO.timeout(5 seconds)) shouldBe Option(2)

  }
}
