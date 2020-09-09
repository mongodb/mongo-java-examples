package connect.tls.cert_store;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Connect to MongoDB with TLS connection.
 * Validate MongoDB server's certificate with the CA certificate. Present Client certificate to be validated by
 * the MongoDB server.
 *
 * CA certificate is stored in the JKS trust store and referenced by the JVM property - `javax.net.ssl.trustStore`
 * Client certificate is stored in the PKCS12 storage and referenced by the JVM property - `javax.net.ssl.keyStore`
 */
public class ValidateServerPresentClientCertificate {

    static {
        String certPath = System.getProperty("cert_path");

        // Refer to the Trust Store storing the CA certificate
        System.setProperty("javax.net.ssl.trustStore", certPath + "/my_trust.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        // Refer to the Key Store storing the Client certificate
        System.setProperty("javax.net.ssl.keyStore", certPath + "/client.keystore.pkcs12");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
    }

    public static void main(String[] args) {

        String connectionString = "mongodb://admin:admin@mongoserver:27017/admin?&ssl=true";

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString)).build();

        MongoClient client = MongoClients.create(settings);

        MongoDatabase test = client.getDatabase("test");
        MongoCollection<Document> coll  = test.getCollection("coll");

        // Retrieve the first document and print it
        System.out.println(coll.find().first());
    }
}
