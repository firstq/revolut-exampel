package utils

import com.byteslounge.slickrepo.repository.Repository
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import persistence.entities.{Transfer, Account}
import persistence.repositories.{AccountRepository, TransferRepository}
import slick.dbio.DBIO

import scala.concurrent.Future


trait Profile {
	val profile: JdbcProfile
}


trait DbModule extends Profile{
	val db: JdbcProfile#Backend#Database

	implicit def executeOperation[T](databaseOperation: DBIO[T]): Future[T] = {
		db.run(databaseOperation)
	}

}

trait PersistenceModule {
	val accountsDal: Repository[Account, Int]
	val transfersDal: Repository[Transfer, Int]
}


trait PersistenceModuleImpl extends PersistenceModule with DbModule{
	this: Configuration  =>

	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2db")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

	override val accountsDal = new AccountRepository(profile)
	override val transfersDal = new TransferRepository(profile)
}
