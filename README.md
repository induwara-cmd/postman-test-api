## **Smart Campus: Sensor \& Room Management API**





#### ***Project Overview***



**This project is a high-performance RESTful API developed for the University's "Smart Campus" initiative. Built using Java JAX-RS (Jersey) and Maven, the system manages a network of thousands of Rooms and Sensors (CO2, Temperature, etc.). The architecture follows advanced REST principles, utilizing the Sub-Resource Locator pattern for reading logs and Exception Mapping for robust error handling.**



#### ***Build \& Launch Instructions***



**To build and run this API locally, follow these steps:**



1. **Clone the Repository: git clone https://github.com/induwara-cmd/postman-test-api.git**
2. **Navigate to Directory: cd <project-folder>**
3. **Build with Maven: mvn clean install**
4. **Run the Server: mvn exec:java (or use your specific Main class command).**
* &#x09;**The API will be accessible at: http://localhost:8080/api/v1**



#### ***Postman Testing***



***STEP 01 :***

* ***A JSON response containing metadata like the version ("v1") and links to your primary collections (rooms and sensors). This demonstrates HATEOAS for your report.***
* GET - http://localhost:8080/api/v1



***STEP 02 :***

* ***Create a Room***
* POST - http://localhost:8080/api/v1/rooms
* Body - {"id": "LIB-301", "name": "Library Quiet Study", "capacity": 50}



* GET - http://localhost:8080/api/v1/rooms/LIB-301
* ***The full JSON object of the room you just created.***



***STEP 03 :***

* ***Add a Sensor***
* POST - http://localhost:8080/api/v1/sensors
* Body - {"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "LIB-301"}



* ***Add a sensor with a invalid roomId.***
* POST - http://localhost:8080/api/v1/sensors
* Body - {"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "NON-EXISTENT"}



* GET - http://localhost:8080/api/v1/sensors?type=Temperature
* ***Show that only sensors matching that type are returned.***



***STEP 04 :***

* ***Update the currentValue.***
* POST - http://localhost:8080/api/v1/sensors/TEMP-001/read
* Body - {"value": 24.5}
* GET - http://localhost:8080/api/v1/sensors/TEMP-001



* ***Update the sensor status to "MAINTENANCE".***
* PUT - http://localhost:8080/api/v1/sensors/TEMP-001
* Headers - Content-Type to application/json
* Body - {"id": "TEMP-001", "type": "Temperature", "status": "MAINTENANCE", "currentValue": 24.5, "roomId": "LIB-301"}



* ***Add a reading to the "MAINTENANCE" sensor.***
* POST - http://localhost:8080/api/v1/sensors/TEMP-001/read
* Body - {"value": 26.5}



***STEP 05 :***

* ***Return the sensor status to "ACTIVE".***
* PUT - http://localhost:8080/api/v1/sensors/TEMP-001
* Headers - Content-Type to application/json
* Body - {"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "currentValue": 24.5, "roomId": "LIB-301"}



* ***Delete the room with the ACTIVE sensor.***
* DELETE - http://localhost:8080/api/v1/rooms/LIB-301



* ***Delete the sensor first and then delete the room.***
* DELETE - http://localhost:8080/api/v1/sensors/TEMP-001
* DELETE - http://localhost:8080/api/v1/rooms/LIB-301





### ***Conceptual Report***



* ##### PART 01 



**Q01 -** 

JAX-RS resource classes by default have the request scope and, hence, a different resource instance will be created for each request that comes in. Using the request scope will prevent resource class instances from having shared mutable state and thereby guaranteeing that the application is threadsafe.



Even though resource classes like the RoomsResource and SensorsResource will be created for each request received in my design, any data persistence is done using the CampusStore singleton. Thus, the data will persist across all requests.



To prevent possible race conditions, guarantee consistency of data, and prevent data loss even though the use of default JAX-RS lifecycle, thread-safe classes like ConcurrentHashMap and CopyOnWriteArrayList have been used. Synchronized blocks have been added to manipulate room-sensor associations.



**Q02 -**

Hypertext-driven discovery, or Hypermedia (HATEOAS), is seen as a characteristic of RESTful APIs due to the presence of navigational links within responses, leading clients to dynamically navigate the API.

&#x09;

My Discovery endpoint provides links for resources like room and sensor, enabling clients to discover available actions independently of any external documentation.



Advantages of HATEOAS over static documentation:

&#x09;Increases discoverability.

&#x09;Decreases hard-coded endpoint configurations for clients.

&#x09;Facilitates the evolution of APIs without disrupting clients.

&#x09;Permits clients to discover resources using embedded navigational links.



This results in a more descriptive and loosely coupled API.



* ##### PART 02



**Q01 -**

The advantage of sending only room ID information is that it will result in smaller payloads and save bandwidth.



Sending complete objects (as I have implemented), on the other hand, will reduce the number of calls required by the client application to obtain extra information about each room, like the name of the room, room capacity, and sensors available.



Trade-Off:

&#x09;Room IDs Only: Small payload but more client requests.

&#x09;Complete Objects: Large payload but few client requests.



In the context of this API, using the latter option will be better.



**Q02 -**

Yes, DELETE is an idempotent method.



In my solution:

&#x09;A first successful DELETE request deletes the room.

&#x09;Subsequent DELETE requests do not alter anything else because the room is already deleted.



Subsequent delete requests may show up as “not found,” but the state of the system will be left untouched once the room is deleted.



Furthermore, a room cannot be deleted if there are any working sensors, ensuring data consistency.



* ##### PART 03



**Q01 -**

The @Consumes(MediaType.APPLICATION\_JSON) annotation ensures that only JSON content can be used at this endpoint.



Any other content type such as,

&#x09;text/plain

&#x09;application/xml



would be considered invalid by JAX-RS, as there is no appropriate MessageBodyReader available for processing such media types, and an error code 415 would be thrown.



This helps maintain the integrity of the input stream by filtering out any unaccepted forms of data.



**Q02 -**

The use of:

&#x09;/sensors?type=CO2



is preferable to:

&#x09;/sensors/type/CO2



as query parameters are meant for searching/fetching from an existing collection while path parameters denote a hierarchical structure.



Benefits of query parameters:

&#x09;Correct usage semantically speaking.

&#x09;Ability to combine multiple filters (type and status perhaps in the future).

&#x09;One collection route rather than numerous specialized routes.

&#x09;Greater flexibility and scalability.



This results in more RESTful design.



* ##### PART 04



**Q01 -**

Sub-Resource Locator Pattern promotes modularity through the distribution of nested resource logic in other classes.



From my example:

&#x09;SensorsResource deals with sensors.

&#x09;SensorReadingsResource deals with sensor readings.



Advantages include:

&#x09;Better separation of concerns.

&#x09;More readable code.

&#x09;Easier maintenance.

&#x09;Improved scalability as nested resources grow in number.

&#x09;Avoidance of a single complex controller.



This design is superior to having all nested resources in one class since it greatly simplifies things.



**Q02 -**

When a reading is added, the API not only stores the history of readings but also updates the currentValue of the parent sensor.



The side effect ensures that:

&#x09;Past data is preserved.

&#x09;The current value of the sensor reflects the most recent reading.

&#x09;Data consistency is maintained when clients request either sensor data or reading history.



* ##### PART 05



**Q02 -**

HTTP 422 is more semantic because the request being made is itself a proper JSON format, though its data is logically incorrect.



For example, adding a sensor with an invalid roomId:

&#x09;Proper JSON syntax.

&#x09;Endpoint is there.

&#x09;Issue is logical dependency.



Using 404 would indicate that the resource was not found, when in actuality, the resource does exist.



422 thus more correctly describes the problem.



**Q04 -**

Making java stack traces available poses security threats as the hackers will know about:

&#x09;Package hierarchy

&#x09;Class names

&#x09;Versions of frameworks used

&#x09;File locations

&#x09;Details of server implementations

&#x09;Possible vulnerabilities



My global exception handler avoids such problems by providing generic secure 500 error messages.



**Q05 -**

JAX-RS filters for logging is preferred over adding logging to each resource method due to logging being a cross-cutting concern.



Advantages:

&#x09;Logics for logging are centralized.

&#x09;Code duplication is avoided.

&#x09;All requests and responses are uniformly logged.

&#x09;Resource methods remain dedicated to business logic only.

&#x09;Easy to maintain and improve in the future.



That’s the reason behind implementing request and response filters for observability.

