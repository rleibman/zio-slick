# ZIO-Slick

| CI | Release |
| --- | --- |
| [![Build Status][Badge-Travis]][Link-Travis] | [![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases] |

Integration between Slick and ZIO

# Getting started

Add zio-slick dependency to your `build.sbt`:

`libraryDependencies += "net.leibman" %% "zio-slick" % "0.1.0"`

# Example of usage:

```
    //An example of a Slick DBIO call.
      val action: DBIO[Int] = {
        import profile.api._
        sql"select 1".as[Int].head
      }


    // Create a database
    val db =
      profile.api.Database.forDriver(org.h2.Driver.load(), "jdbc:h2:mem:")
      
    // Create a zio runtime that has a Slick Database (we add the clock so we can do time outs)
    val dbRuntime =
      Runtime(new SlickDatabase with Clock.Live { val database = db }, PlatformLive.Default)

    val testRuntime = Runtime(12, PlatformLive.Default)

    dbRuntime.unsafeRun(action) // shouldBe 1

    val sumOfZIO: SlickZIO[Int] = for {
      a <- SlickZIO(action)
      b <- SlickZIO(action)
    } yield a + b

    dbRuntime.unsafeRun(sumOfZIO) // shouldBe 2

    // Do it with a timeout (it returns Option[Int] instead of Int.

    import scala.concurrent.ExecutionContext.Implicits.global

    val sumOfDBIO: DBIO[Int] = for {
      a <- action
      b <- action
    } yield a + b

    dbRuntime.unsafeRun(sumOfDBIO.timeout(5 seconds)) // shouldBe Option(2)

```


[Link-Travis]: https://travis-ci.com/rleibman/zio-slick "circleci"
[Link-SonatypeReleases]: https://oss.sonatype.org/content/repositories/releases/net/leibman/zio-slick-core_2.12/ "Sonatype Releases"

[Badge-Travis]: https://travis-ci.com/rleibman/zio-slick.svg?branch=master "Codecov" 
[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/oss.sonatype.org/net.leibman/zio-slick-core_2.12.svg "Sonatype Releases"