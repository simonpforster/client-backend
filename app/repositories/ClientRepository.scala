package repositories

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Updates.{combine, set, unset}
import common.DBKeys
import models._
import org.mongodb.scala.model.Filters.{equal, exists}
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[Client](
  collectionName = DBKeys.clientCollection,
  mongoComponent = mongoComponent,
  domainFormat = Client.format,
  indexes = Seq(
    IndexModel(ascending(DBKeys.crn), IndexOptions().unique(true)),
    IndexModel(ascending(DBKeys.arn), IndexOptions().unique(false).sparse(true))
  )) {

  def create(client: Client): Future[Boolean] = collection.insertOne(client).toFuture().map {
    response => response.wasAcknowledged && !response.getInsertedId.isNull
  } recover { case _ => false }

  def read(crn: String): Future[Option[Client]] = collection.find(equal(DBKeys.crn, crn)).headOption()

  def readAllAgent(arn: String): Future[List[Client]] =
    collection.find(equal(DBKeys.arn, arn)).toFuture().map {
      _.toList
    }

  def delete(crn: String): Future[Boolean] =
    collection.deleteOne(equal(DBKeys.crn, crn)).toFuture().map {
      response => response.wasAcknowledged && response.getDeletedCount == 1
    }

  def addAgent(crn: String, arn: String): Future[(Boolean, Boolean)] =
    collection.find(equal(DBKeys.crn, crn)).toFuture().flatMap { x =>
      if (x.length == 1) {
        collection.updateOne(and(equal(DBKeys.crn, crn), exists(DBKeys.arn, exists = false)), set(DBKeys.arn, arn))
          .toFuture().map { response => if (response.wasAcknowledged && response.getModifiedCount == 1) (true, true) else (true, false) }
      }
      else Future((false, true))
    }

  def removeAgent(crn: String, arn: String): Future[(Boolean, Boolean)] =
    collection.find(equal(DBKeys.crn, crn)).toFuture().flatMap { x =>
      if (x.length == 1) {
        collection.updateOne(and(equal(DBKeys.crn, crn), equal(DBKeys.arn, arn)), unset(DBKeys.arn))
          .toFuture().map { response => if (response.wasAcknowledged && response.getModifiedCount == 1) (true, true) else (true, false) }
      }
      else Future(false, true)
    }

  def updateName(crn: String, newName: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal(DBKeys.crn, crn),
      set(DBKeys.name, newName)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())

  }

  def updateBusinessType(crn: String, newBusinessType: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal(DBKeys.crn, crn),
      set(DBKeys.businessType, newBusinessType))
      .toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def updateContactNumber(crn: String, newContactNumber: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal(DBKeys.crn, crn),
      set(DBKeys.contactNumber, newContactNumber))
      .toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def updateProperty(crn: String, newPropertyNumber: String, newPostcode: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal(DBKeys.crn, crn),
      combine(set(DBKeys.propertyNumber, newPropertyNumber),
        set(DBKeys.postcode, newPostcode))
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }
}


