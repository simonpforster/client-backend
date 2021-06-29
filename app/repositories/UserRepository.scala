package repositories

import models.User
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions}
import org.mongodb.scala.model.Indexes.ascending
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserRepository  @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[User](
	collectionName = "users",
	mongoComponent = mongoComponent,
	domainFormat   = User.format,
	indexes        = Seq(
		IndexModel(ascending("crn"), IndexOptions().unique(true)
		))
){
  def create(user: User): Future[Boolean] = collection.insertOne(user).toFuture().map{
		response => if(response.wasAcknowledged && response.getInsertedId != null) true
		else false
	}

	def login(user: User): Future[Boolean] = ???

	def read(crn: String): Future[Option[User]] = collection.find(Filters.eq("crn", crn)).headOption()

	def delete(crn: String): Future[Boolean] = ???
}
