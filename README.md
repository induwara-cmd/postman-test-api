## **Smart Campus: Sensor \& Room Management API**





#### ***Project Overview***



**This project is a high-performance RESTful API developed for the University's "Smart Campus" initiative. Built using Java JAX-RS (Jersey) and Maven, the system manages a network of thousands of Rooms and Sensors (CO2, Temperature, etc.). The architecture follows advanced REST principles, utilizing the Sub-Resource Locator pattern for reading logs and Exception Mapping for robust error handling.**



#### ***Build \& Launch Instructions***



**To build and run this API locally, follow these steps:**



1. **Clone the Repository: git clone <your-repo-link>**
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
* POST - http://localhost:8080/api/v1/TEMP-001/read
* Body - {"value": 24.5}
* GET - http://localhost:8080/api/v1/TEMP-001



* ***Update the sensor status to "MAINTENANCE".***
* PUT - http://localhost:8080/api/v1/sensors/TEMP-001
* Headers - Content-Type to application/json
* Body - {"id": "TEMP-001", "type": "Temperature", "status": "MAINTENANCE", "currentValue": 24.5, "roomId": "LIB-301"}



* ***Add a reading to the "MAINTENANCE" sensor.***
* POST - http://localhost:8080/api/v1/TEMP-001/read
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



