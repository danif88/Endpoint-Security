# Endpoint-Security

## What is it?
This is a REST service that allows you to add security to your endpoint.

## How to compile it?
```sh
$ mvn package
```

## How to run it?
```sh
$ java -jar target/gs-rest-service-complete.jar
```

## How to test that is working?
```sh
http://localhost:8080/logIn?name=root&encrypted=81dc9bdb52d04dc20036dbd8313ed055
```
The password is encrypted with md5
root password 1234
The users are specified in the users.xml file

## API Methods
To see all the methos of the API you should see the Controller javadoc. 



