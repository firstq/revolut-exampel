package entities

import persistence.entities.{SimpleAccount, Account, SimpleTransfer, Transfer}
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {

  implicit val accountFormat = jsonFormat3(Account)
  implicit val simpleAccountFormat = jsonFormat2(SimpleAccount)

  implicit val transferFormat = jsonFormat4(Transfer)
  implicit val simpleTransferFormat = jsonFormat3(SimpleTransfer)
}