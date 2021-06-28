package repositories

import com.mongodb.client.model.IndexOptions
import models.User
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import org.mongodb.scala.model.Indexes.ascending
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[User](
  collectionName = "users",
  mongoComponent = mongoComponent,
  domainFormat   = User.format,
  indexes        = Seq(
    IndexModel(ascending("crn"), IndexOptions().unique(true)
    ))
){

  def create(user: User): Future[Boolean] = ???

  def read(crn: String): Future[User] = ???

  def update(updatedUser: User): Future[Boolean] = ???

  def delete(crn: String): Future[Boolean] = ???
}
