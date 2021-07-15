package models

import play.api.libs.json.{Json, OFormat}


  case class NameUpdateDetails(crn: String,
                               name: String)

  object NameUpdateDetails {
    implicit val format: OFormat[NameUpdateDetails] = Json.format[NameUpdateDetails]
  }

  case class ContactNumberUpdateDetails(crn: String,
                                        contactNumber: String)

  object ContactNumberUpdateDetails {
    implicit val format: OFormat[ContactNumberUpdateDetails] = Json.format[ContactNumberUpdateDetails]
  }

  case class BusinessTypeUpdateDetails(crn: String, businessType: String)

  object BusinessTypeUpdateDetails {
    implicit val format: OFormat[BusinessTypeUpdateDetails] = Json.format[BusinessTypeUpdateDetails]
  }

  case class PropertyUpdateDetails(crn:String,
                                   propertyNumber:String,
                                   postcode:String
                                  )

  object PropertyUpdateDetails {
    implicit val format: OFormat[PropertyUpdateDetails] = Json.format[PropertyUpdateDetails]
  }

