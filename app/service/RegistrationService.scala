package service

import models.{Client, ClientRegistration, EncryptedPassword, User}
import repositories.{ClientRepository, UserRepository}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationService @Inject()(userRepo: UserRepository, clientRepo: ClientRepository, crypto: EncryptionService) {
  def register(client: ClientRegistration): Future[Option[Client]] = {
    val crn: String = generateCRN()
    val nonce: Array[Byte] = crypto.getNonce
    val secretPass: Array[Byte] = crypto.encrypt(client.password.getBytes(), crypto.getKey, nonce)
    val ePassword: EncryptedPassword = EncryptedPassword(secretPass, nonce)
    val newClient = Client(crn, client.name, client.businessName, client.contactNumber, client.propertyNumber, client.postcode, client.businessType)
    for {
      createClient <- clientRepo.create(newClient)
      createUser <- userRepo.create(User(crn, ePassword))
    } yield (createClient, createUser) match {
      case (true, true) => Some(newClient)
      case _ => None
    }
  }
  def generateCRN(): String = {
    val id = UUID.randomUUID().toString.replace("-", "")
    "CRN" + id.substring(id.length() - 8).toUpperCase()
  }
}
