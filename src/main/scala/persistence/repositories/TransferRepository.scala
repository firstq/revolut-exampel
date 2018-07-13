package persistence.repositories

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import persistence.entities.Transfer
import slick.ast.BaseTypedType
import slick.driver.JdbcProfile

class TransferRepository(override val driver: JdbcProfile) extends Repository[Transfer, Int](driver) {
  import driver.api._
  val pkType = implicitly[BaseTypedType[Int]]
  val tableQuery = TableQuery[Transfers]
  type TableType = Transfers

  class Transfers(tag: slick.lifted.Tag) extends Table[Transfer](tag, "TRANSFERS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def from = column[Int]("from")
    def value = column[BigDecimal]("value")
    def to = column[Int]("to")
    def * = (id.?, from, value, to) <> ((Transfer.apply _).tupled, Transfer.unapply)
  }
}