# My social media application

A backend application created to demonstrate the creation of a spring boot microservice using scala.

### Context:

I have a lot of experience with spring boot, but using kotlin. My experience with scala is 
in the context of Apache Spark. I need to "learn" how to create microservices using spring boot 
and scala. It wasn't so difficult to learn, because it's very similar to kotlin-way.

## Chooses:

### 1) Gradle

I have been using gradle on last 4 years, so it's my natural choice. I particularly
believe that it fits very well with spring-boot. It's able to lead all build process,
including the docker image generation and publishing. The project is packaged with the
gradle wrapper, so it's easier to run the build process without the necessity
to install gradle outside the project. I

### 2) JDK 16

It's the JDK version that I chose to develop the app locally. For the packaged app in docker I use JRE 11 (best version available in paketo).

### 3) Spring boot

I am fascinated about the facilities that spring boot brings to your life. In my opinion,
it's a natural choice when you are talking about JVM applications. It integrates
with all major frameworks, and you can use it easily, bacause auto-configuration 
feature.

### 4) Rest level 2 (Richardson Maturity Model)

I have been working with REST on the last 10 years, at least. I tried to use REST by the bookmark
a lot of times, and I have had some bad experience with granular REST endpoints. Rest level 2 fits well when you are 
talking about APIs that are providing resouces in a low level. When you are talking abou complex applications, that exposes 
services that are involved in a complex enviroment, the REST level 3 fits better.
Currently, I prefer to use REST level 3 in almost every case. Moreover, I like to use 
[Rest Without PUT](https://www.thoughtworks.com/radar/techniques/rest-without-put), using just GET and POST http methods and
command and quey separation. This style works to complex and simple scenarios. 

However, in current project, I preffered to use REST level 2. For educational purposes and to expose low granular funcionaties,
how is the case, REST level 2 is my preffered choice.

### 5) Cucumber

BDD, that I like to call [specification by example](https://www.amazon.com.br/Specification-Example-Successful-Deliver-Software/dp/1617290084),
for me, it's something crutial for a project success. It's the most important type of test of a backend application, that gives to you
feedback about the funcionaties of your application thus about how well written your code are. I experienced in one project the practice of 100% unit testing
and, besides of the benefits, it's not something that I would do without a very good reason. Considering the costs and the benefits of, actually I beliave the
best model of testing is the [trophy model](https://pbs.twimg.com/media/DVUoM94VQAAzuws.jpg).

I consider specification by example a kind of integration test, that give the test benefits of you app, but offer other amazing benefit: document the
requirements of your app basing in examples.

I have experience using cucumber and concordion to this kind of test, but recently I have been using cucumber. It has a very good integration with 
spring boot and Intellij.

I have chosen to use an in memory database (h2) to the acceptance tests and when you run the app locally. The app uses mysql in docker-compose locally
or in AWS.

Considering the best practices about testing, you should always start your test with an "empty" state. So I created a class CleanDatabaseBetweenScenarios
with the purpose to clean all database between a scenario and other. Therefore, you always need to configure the "Given state" of your test.

The cucumber features are in the folder src/test/resources. There are three features there: post, comment and user. 

I focused in creating this BDD tests for almost all the code. I created a unit testing just for the feature of user mentioning, because
there was a very specific functionality provided by the class.

I configured jacoco in gradle to collect coverage metrics, and I have had a result of 80%. I think there is some more features to coverage,
but there is a schedule that I need to follow. However, 80% it's a good number.

### 6) JDBC

Aiming for simplification, I chose not to use JPA (Hibernate). I used pure JDBC (using spring). This approach has advantages and disadvantages,
but considering NoSQL databases, and pure Domain Driven Design, JPA it's not the silver bullet anymore. Not to use JPA is a plausible option.

The use of JDBC facilitates the use of pure DDD, because you don't need to carry about to fit in a JPA entity in a domain model. Besides that,
It's very commom to use other types of database. I, for example, have been using Elastic and ArangoDB a lot and in this cases there isn't 
a "JPA" option. 

However, it's need to be clear, I need to write a lot of code that I am not used to when I'm using JPA. 

### 7) User UID

Aiming for simplification, I chose not to implement any security feature. It would be very onerous. So when you registir a new user, it's generated
a UID, using username, mail and password. This UID is the paremeter used in the other endpoints to say what user we are dealing to. The correct way 
would se something similiar to a token jwt, that would be used to identify the user securily. 

The option for use the UID was to avoid sending clear username in the body. 

### 8) Image processing

I chose just to rescale the image and downsize the image quality as way of reach the requirements. Again, aiming for simplification, when I am
rescaling the image, I fixed the with in 128px. There wasn't enhough time to create an asynchronous queue to deal with image processing.
For a real production application, It is something necessary.

### 9) Clean code

I really beliave in clean code as an art. It's a skill that you need to develop to create susteinable code. When you reach this level, 
commenting code it's not so necessary, at least in parts not so critical. Because of that, I prefered to keep the code clean without
comment methods or endpoints. I hope that my code is really clear :grin:.

### 10) Mention notification

I haven't had enough time to create a notification module for users, but I prrsisted this in information in database. It's easier 
to extend the app to do it.

### 11) Blob field

I chose to store the image content in the mysql database. I don't think that SQL databases are the best option to store images, I would prefer to
store the images in a filesystem service, for example, but to conclude this challenge I need to cut some desirable requirements.

### 12) Paketo

I configured my gradle file to use packeto to build and publish my backend image. This possibility cames easily with the use of gradle and spring boot, you need 
just some lines of configuration. Because of that my project don't have a Dockerfile. However, the docker compose file is necessary to publish the app in AWS (and to run the packaged app locally).

### 13) AWS

All my experience with clouds are with an on-premise enviroments. For example, here in my current job, we use rancher as a k8s enviroment and our
continuous deployment enviroment is based on jenkins (self-hosted). So, it was a challenge to publish my app in AWS, but I dit it. I used 
an integration that docker compose has with ECS from AWS. You are able to provision all your enviroment in ECS just with "docker compose up". 
This feature made my day easier.


## Commands

### 1) Run tests

**./gradlew test**

### 2) Build project

**./gradlew clean build**

### 3) Generate image

**./gradlew clean test bootBuildImage**

I commented my password to dockerhub in the build.gradle file. Without that it isn't able to publish. 

### 4) Create services in AWS

You need to have AWS cli installed and configured. So it's just run the commands:

**docker context create ecs aws-context**
**docker compose up**

The operation takes a couple of minutes

## Services

Mysql Adminer -> **sprin-LoadB-QUTFPDUMYUH1-776bc0d3b55bb8fc.elb.sa-east-1.amazonaws.com:8080**\
App -> **sprin-LoadB-QUTFPDUMYUH1-776bc0d3b55bb8fc.elb.sa-east-1.amazonaws.com:8090**\
Mysql -> **sprin-LoadB-QUTFPDUMYUH1-776bc0d3b55bb8fc.elb.sa-east-1.amazonaws.com:3306**

Obs: Accessing the [Adminer](http://sprin-LoadB-QUTFPDUMYUH1-776bc0d3b55bb8fc.elb.sa-east-1.amazonaws.com:8080) you will
be able to see the effects on the database after REST operations (server: mysql, user: root, password: root, database: MYSOCIALMEDIA)

## Endpoints

There is a file **Insominia.json** in the root directory. This file should be imported in insomnia and it will have
the basic endpoints configured. This can facilitate the interaction with the service.

#### Register new user
```
POST ${URL}/user/registration

Request:
{
    "username": "dharlanoliveira2",
    "email": "dh2@gmail.com",
    "password": "teste123"
}

Response:
{"uid":"ae33514d53965403262104c7f1137106"}
```

#### Create new post

```
POST ${URL}/posts

Request:
{
	"userUid": "ae33514d53965403262104c7f1137106",
	"text": "My first post",
	"imageBase64": "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"
}

Response:
{
  "id": 1
}
```

#### Update post

```
PATCH ${URL}/posts/1

Request:
{
    "id": 1,
	"userUid": "ae33514d53965403262104c7f1137106",
	"text": "Changing post text"
}
```

#### Create new comment

```
POST ${URL}/posts/1/comments

Request:
{
	"userUid": "ae33514d53965403262104c7f1137106",
	"text": "Comment mentioning myself @dharlanoliveira2",
	"postId": 1
}

```

#### Update comment
```
PATCH ${URL}/posts/1/comments/1
{
    "userUid": "ae33514d53965403262104c7f1137106",
    "text": "Comment mentioning myself @dharlanoliveira2 again",
    "postId": 1
}
```

#### List all users posts

```
POST ${URL}/posts

Response:
[
  {
    "username": "dharlanoliveira2",
    "text": "My first post",
    "instant": "2021-10-25T04:52:03",
    "comments": [],
    "imageBase64": "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"
  }
]

```

#### List posts from user

```
POST ${URL}/posts?userUid=ae33514d53965403262104c7f1137106

Response:
[
  {
    "username": "dharlanoliveira2",
    "text": "My first post",
    "instant": "2021-10-25T04:52:03",
    "comments": [],
    "imageBase64": "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"
  }
]

```

#### List posts from user with ordering

```
POST ${URL}/posts?userUid=ae33514d53965403262104c7f1137106&order=ASC

Response:
[
  {
    "username": "dharlanoliveira2",
    "text": "My first post",
    "instant": "2021-10-25T04:52:03",
    "comments": [],
    "imageBase64": "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"
  }
]

```

#### Get post image

```
GET ${URL}/posts/1/image

Response:
Image

```

#### Delete post

```
DELETE ${URL}/posts/1/image

{
"id": 1,
"userUid": "ae33514d53965403262104c7f1137106"
}
```

#### Delete comment

```
DELETE ${URL}/posts/1/image

{
	"id": 1,
	"postId": 1,
	"userUid": "ae33514d53965403262104c7f1137106"
}
```


