package repositories

import models.Client
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[Client](
  collectionName = "clients",
  mongoComponent = mongoComponent,
  domainFormat = Client.format,
  indexes = Seq(
    IndexModel(ascending("crn"), IndexOptions().unique(true)
    ))
) {

  def create(client: Client): Future[Boolean] = collection.insertOne(client).toFuture().map(_ => true).recover { case _ => false }

  def read(crn: String): Future[Client] = ???

  def readAll(): Future[List[Client]] = ???

  def readAllAgent(arn: String): Future[List[Client]] = ???

  def update(updatedClient: Client): Future[Boolean] = ???

  def delete(crn: String): Future[Boolean] = ???
}
