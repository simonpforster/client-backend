package repositories

import common.DBKeys
import models.User
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions}
import org.mongodb.scala.model.Indexes.ascending
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[User](
  collectionName = DBKeys.userCollection,
  mongoComponent = mongoComponent,
  domainFormat = User.format,
  indexes = Seq(
    IndexModel(ascending(DBKeys.crn), IndexOptions().unique(true)
    ))) {
  def create(user: User): Future[Boolean] = collection.insertOne(user).toFuture().map {
    response => response.wasAcknowledged && response.getInsertedId != null
  } recover { case _ => false }

  def read(crn: String): Future[Option[User]] = collection.find(Filters.eq(DBKeys.crn, crn)).headOption()

  def delete(crn: String): Future[Boolean] = collection.deleteOne(equal(DBKeys.crn, crn)).toFuture().map {
    response => response.wasAcknowledged && response.getDeletedCount == 1
  }
}
