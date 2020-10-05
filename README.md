# mongo-java-examples
This repository contains examples that demonstrate usage of [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/) with MongoDB database.

## Examples Description:

### Establish connection over TLS/SSL with MongoDB cluster
**Package:** `org.mongodb.connect.tls`

This package contains several examples demonstrating [MongoClient](https://mongodb.github.io/mongo-java-driver/4.1/apidocs/mongodb-driver-sync/com/mongodb/client/MongoClient.html) configuration and establishment of [TLS connectivity between the MongoDB database and MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/4.1/driver/tutorials/ssl/)

The code has been tested with:
- MongoDB Java Driver (`mongodb-driver-sync`) [v4.1.0](https://mongodb.github.io/mongo-java-driver/4.1/)
- JRE 1.8

**The following TLS connectivity use cases covered:**
- Java Application configured with CA certificate to validate the MongoDB Server's certificate
- Java Application validating the MongoDB server’s certificate and providing its Client’s certificate to be validated by the MongoDB Server
- As in the previous case of validating and providing certs, but with the additional [authentication](http://mongodb.github.io/mongo-java-driver/4.1/driver-reactive/tutorials/authentication/) of the database by using the Client’s X.509 certificate - [X509 authentication](https://docs.mongodb.com/manual/core/security-x.509/#x-509)

Certs from a known Certificate Authority are usually stored in the `cacerts` file provided with the JRE. The use cases in the following examples assume the CA certificates were issued privately, and were not issued by a known Certificate Authority.

The classes implementing the use cases are separated into two packages: 
- `org.mongodb.connect.tls.cert_store`
- `org.mongodb.connect.tls.sslcontext`

Classes in the `org.mongodb.connect.tls.cert_store` demonstrate how to set the location of the certificate files by using the following JVM system properties:
- `javax.net.ssl.trustStore` - points to the JKS Trust Store file holding the CA certificate
- `javax.net.ssl.keyStore` - points to the PKCS12 Key Store file holding the Client's certificate

Classes in the `org.mongodb.connect.tls.sslcontext` demonstrate implementation of the same use cases, but with the [MongoClient](https://mongodb.github.io/mongo-java-driver/4.1/apidocs/mongodb-driver-sync/com/mongodb/client/MongoClient.html) object configured to use a custom [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html).