# mongo-java-examples
This repository contains examples that demonstrate usage of [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/) with MongoDB database.

### Establish connection over TLS/SSL with MongoDB cluster
**Package:** `org.mongodb.connect.tls`

This package contains several examples demonstrating [MongoClient](https://mongodb.github.io/mongo-java-driver/4.1/apidocs/mongodb-driver-sync/com/mongodb/client/MongoClient.html) configuration and establishment [TLS connectivity between the MongoDB database and MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/4.1/driver/tutorials/ssl/)

The code tested with:
- MongoDB Java Driver (`mongodb-driver-sync`) [v4.1.0](https://mongodb.github.io/mongo-java-driver/4.1/)
- JRE 1.8

**The following TLS connectivity use cases covered:**
- Java Application needs to be configured with CA certificate to validate the MongoDB Server's certificate
- Java Application must validate the MongoDB server's certificate and to provide its Client's certificate to be validated by the MongoDB Server
- As the previous use case, but additionally [authentication](http://mongodb.github.io/mongo-java-driver/4.1/driver-reactive/tutorials/authentication/) with the database is made by using the Client's certificate - [X509 authentication](https://docs.mongodb.com/manual/core/security-x.509/#x-509)

In these use cases the assumption is that the CA certificate was issued privately, oppositely to the CA certificate issued by a known CA organization, as such certificates are usually stored in the `cacerts` file provided with the JRE. 

The classes implementing the use cases separated into two packages: 
- `org.mongodb.connect.tls.cert_store`
- `org.mongodb.connect.tls.sslcontext`

Classes in the `org.mongodb.connect.tls.cert_store` demonstrate configuration in which location of the certificates defined by setting the following JVM system properties:
- `javax.net.ssl.trustStore` - points to the JKS Trust Store file holding the CA certificate
- `javax.net.ssl.keyStore` - points to the PKCS12 Key Store file holding the Client's certificate

Classes in the `org.mongodb.connect.tls.sslcontext` demonstrate implementation of the same use cases but [MongoClient](https://mongodb.github.io/mongo-java-driver/4.1/apidocs/mongodb-driver-sync/com/mongodb/client/MongoClient.html) object configured with the custom [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html)
