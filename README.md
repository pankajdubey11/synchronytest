Imgur API Integration Application
This application provides APIs to interact with the Imgur API for uploading, viewing, and deleting images after successful user authentication.

Technologies Used
Spring Boot
Swagger for API testing
MapStruct for property mapping
Setup
Clone the repository.

Configure the server port to 8080 in the application.yaml file.

Build and run the application.

Access Swagger UI for API testing: http://localhost:8080/swagger-ui.html#/

API Endpoints
User Registration - /user/registration (POST)

Upload Image - /user/image (POST)

Response:

json
Copy code
{
  "userImageId": 2,
  "userId": 1,
  "srcImageName": "sign.jpg",
  "imgurImageId": "3gRC71B",
  "imgurImageType": "image/jpeg",
  "imgurImageTitle": "null",
  "imgurImageDesc": "null",
  "imgurImageDeleteHash": "PkBnoiRLa4FA85R",
  "imgurImageLink": "https://i.imgur.com/3gRC71B.jpg",
  "uploadedDate": "2019-06-05T09:50:05.663+0000"
}
View Image - /user/image/getImage (POST)

Request:

json
Copy code
{
  "hash": "string",
  "password": "string",
  "userName": "string"
}
Note: Set the imgurImageId value from the response of the Upload Image API.

Delete Image - /user/image (DELETE)

Request:

json
Copy code
{
  "hash": "string",
  "password": "string",
  "userName": "string"
}
Note: Set the imgurImageDeleteHash value from the response of the Upload Image API.

Get User Details - /user/{userName} (GET)

Additional Configuration
To configure MapStruct in Eclipse, follow the instructions provided in this link.