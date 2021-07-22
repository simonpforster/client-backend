
# Client Backend Service

This service process requests made from the front end services(Client Registration & Client Dashboard) and Agent Backend service.  
based of the requestd made this service will manipulate the data into mongoDb returning the appropriate response,
the key functions this service provides are as follows:

- Register(Used to add a client to the database)
- Reading a client details based of their CRN(Client Reference Number)
- Check if a client exists(Used for Login) 
- Read All Agent (Used to populate agent dashboard with clients associated to the agent)
- Remove Agent(Used to remove the connection between a client and agent)
- Delete Client(Used to delete a client from the database)
- Update Name(Used to update the name of a client in the database)
- Update BusinessType(Used to update the Business Type for a client in the database)
- Update Contact Number(Used to update the Contact Number for a client in the database)
- Update Property(Used to update the Property Number and Postcode for a client in the database)

### How to Setup/run the service:
To run this service you must have the following setup:
- Have nothing running on ports (9006)
- Have scala version: 2.12.13
- Have sbt installed on your computer

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
