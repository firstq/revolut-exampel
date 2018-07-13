package persistence.entities

import com.byteslounge.slickrepo.meta.{Entity, Keyed}

case class SimpleTransfer(from: Int, value: BigDecimal, to: Int)

case class Transfer(override val id: Option[Int], from: Int, value: BigDecimal, to: Int) extends Entity[Transfer, Int]{
  def withId(id: Int): Transfer = this.copy(id = Some(id))
}
