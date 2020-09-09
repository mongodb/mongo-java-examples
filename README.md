# mongo_tls_java
This repository contains several examples demonstrating [MongoClient](https://mongodb.github.io/mongo-java-driver/4.1/apidocs/mongodb-driver-sync/com/mongodb/client/MongoClient.html) configuration and establishment [TLS connectivity between the MongoDB database and MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/4.1/driver/tutorials/ssl/)

The code tested with:
- MongoDB Java Driver (`mongodb-driver-sync`) [v4.1.0](https://mongodb.github.io/mongo-java-driver/4.1/)
- JRE 1.8

The following TLS connectivity use cases are covered:
- Java Application needs to be configured with CA certificate to validate the MongoDB Server's certificate
- Java Application must validate the MongoDB server's certificate and to provide its Client's certificate to be validated by the MongoDB Server
- As the previous use case, but additionally [authentication](http://mongodb.github.io/mongo-java-driver/4.1/driver-reactive/tutorials/authentication/) with the database is made by using the Client's certificate - [X509 authentication](https://docs.mongodb.com/manual/core/security-x.509/#x-509)

In these use cases the assumption is that the CA certificate was issued privately, oppositely to the CA certificate issued by a known CA organization, as such certificates are usually stored in the `cacerts` file provided with the JRE. 

The classes implementing the use cases are separated to two packages: 
- cert_store
- sslcontext

Classes in the `cert_store` demonstrate configuration in which location of the certificates defined by setting the following JVM properties:
- `javax.net.ssl.trustStore` - points to the JKS Trust Store file holding the CA certificate
- `javax.net.ssl.keyStore` - points to the PKCS12 Key Store file holding the Client's certificate

Classes in the `sslcontext` demonstrate implementation of the same use cases but with configuration of the [MongoClient](https://mongodb.github.io/mongo-java-driver/4.1/apidocs/mongodb-driver-sync/com/mongodb/client/MongoClient.html) object with custom [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html)
