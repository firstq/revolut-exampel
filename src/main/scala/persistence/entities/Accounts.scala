package persistence.entities

import com.byteslounge.slickrepo.meta.{Entity, Keyed}

case class SimpleAccount(name: String, balance: BigDecimal)

case class Account(override val id: Option[Int], name: String, balance: BigDecimal) extends Entity[Account, Int]{
  def withId(id: Int): Account = this.copy(id = Some(id))
}
