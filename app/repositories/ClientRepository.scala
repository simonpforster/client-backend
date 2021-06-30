package repositories

import com.mongodb.client.model.Filters.{and, or}
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.set
import models.Client
import org.mongodb.scala.model.Filters.{equal, exists}
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, UpdateOptions}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[Client](
	collectionName = "clients",
	mongoComponent = mongoComponent,
	domainFormat   = Client.format,
	indexes        = Seq(
		IndexModel(ascending("crn"), IndexOptions().unique(true)
		))
){

	def create(client: Client): Future[Boolean] = ???

	def read(crn: String): Future[Option[Client]] = collection.find(Filters.eq("crn", crn)).headOption()

	def readAll(): Future[List[Client]] = ???

	def readAllAgent(arn: String): Future[List[Client]] = ???

	def update(updatedClient: Client): Future[Boolean] = ???

	def delete(crn: String): Future[Boolean] = ???

	def addAgent(crn: String, arn: String): Future[(Boolean, Boolean)] =
		collection.find(equal("crn", crn)).toFuture().flatMap{x => if (x.length == 1) {
			collection.updateOne(and(equal("crn", crn), exists("arn", false)), set("arn", arn))
			.toFuture().map { response => if (response.wasAcknowledged && response.getModifiedCount == 1) (true, true) else (true, false) }}
		else Future((false, true))
	}
	def removeAgent(crn: String, arn: String): Future[(Boolean,Boolean)] =
		collection.find(equal("crn", crn)).toFuture().flatMap{x => if (x.length == 1) {
			collection.updateOne(and(equal("crn", crn), equal("arn", arn)), set("arn", None))
				.toFuture().map { response => if (response.wasAcknowledged && response.getModifiedCount == 1 ) (true, true) else (true, false)}}
		else Future(false, true)}
}


