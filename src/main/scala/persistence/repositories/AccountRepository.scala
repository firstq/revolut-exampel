package persistence.repositories

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import persistence.entities.Account
import slick.ast.BaseTypedType
import slick.driver.JdbcProfile


class AccountRepository(override val driver: JdbcProfile) extends Repository[Account, Int](driver) {
  import driver.api._
  val pkType = implicitly[BaseTypedType[Int]]
  val tableQuery = TableQuery[Accounts]
  type TableType = Accounts

  class Accounts(tag: slick.lifted.Tag) extends Table[Account](tag, "ACCOUNTS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def balance = column[BigDecimal]("balance")
    def * = (id.?, name, balance) <> ((Account.apply _).tupled, Account.unapply)

  }
}