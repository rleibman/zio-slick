import slick.basic.BasicBackend
import slick.dbio.DBIO
import zio.ZIO

package object zioslick {
  trait SlickDatabase {
    val database: BasicBackend#DatabaseDef
  }

  object SlickDatabase {

    object Live {
      def apply(db: BasicBackend#DatabaseDef) = new SlickDatabase {
        override val database = db
      }
    }
  }

  type SlickZIO[T] = ZIO[SlickDatabase, Throwable, T]

  object SlickZIO {
    def apply[T](action: DBIO[T]): SlickZIO[T] = {
      for {
        env <- ZIO.environment[SlickDatabase]
        res <- ZIO.fromFuture(implicit ec => env.database.run(action))
      } yield res

    }
  }

  implicit def DBIO2SlickZIO[T](dbio: DBIO[T]): SlickZIO[T] = SlickZIO[T](dbio)
}
