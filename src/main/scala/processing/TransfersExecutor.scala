package processing

import java.util.concurrent.ConcurrentLinkedQueue
import scala.concurrent._
import ExecutionContext.Implicits.global
import persistence.entities.{Account, SimpleTransfer, Transfer}
import utils.{ActorModule, Configuration, DbModule, PersistenceModule}


class TransfersExecutor(transfersQueue: ConcurrentLinkedQueue[SimpleTransfer],
                        modules: Configuration with PersistenceModule with DbModule with ActorModule) extends Runnable{



  override def run(): Unit = {
    while (!transfersQueue.isEmpty) {
      val simpleTransfer = transfersQueue.poll()
      println(simpleTransfer)
      modules.db.run(
        modules.accountsDal.findOne(simpleTransfer.from)
          .map(a => {
            modules.db.run(modules.accountsDal.update(Account(Some(simpleTransfer.from),a.get.name,a.get.balance - simpleTransfer.value)))
          }))

      modules.db.run(
        modules.accountsDal.findOne(simpleTransfer.to)
          .map(a => {
            modules.db.run(modules.accountsDal.update(Account(Some(simpleTransfer.to),a.get.name,a.get.balance + simpleTransfer.value)))
          }))

      modules.db.run(modules.transfersDal.save(new Transfer(None, simpleTransfer.from, simpleTransfer.value, simpleTransfer.to)))

    }
  }

  def enquequeToExecute(transfer: SimpleTransfer): Boolean = transfersQueue.add(transfer)

}

