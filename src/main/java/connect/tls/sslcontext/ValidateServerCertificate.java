package connect.tls.sslcontext;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.SslSettings;
import org.bson.Document;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Connect to MongoDB with TLS connection. Validate MongoDB server's certificate with the CA certificate.
 *
 * Create {@link javax.net.ssl.SSLContext} with the TrustStore holding the CA certificate and
 * provide it to the MongoDB Driver
 *
 * Client certificate is not be expected to be presented to establish the TLS connection
 */
public class ValidateServerCertificate {

    public static void main(String[] args) {

        String connectionString = "mongodb://admin:admin@mongoserver:27017/admin?&ssl=true";

        SSLContext sslContext;
        try {
            sslContext = getSSLContext();
        } catch (Exception e) {

            System.out.println("Failed to generate SSLContext. Error: " + e.getMessage());
            return;
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .applyToSslSettings(builder -> {
                    builder.context(sslContext);
                })
                .build();

        MongoClient client = MongoClients.create(settings);

        MongoDatabase test = client.getDatabase("test");
        MongoCollection<Document> coll  = test.getCollection("coll");

        // Retrieve the first document and print it
        System.out.println(coll.find().first());
    }

    /**
     * Load CA certificate from the file into Trust Store and generate the {@link SSLContext}
     *
     * @return SSLContext
     *
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    private static SSLContext getSSLContext() throws IOException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        // Path to the CA certificate on disk
        String caCertPath = System.getProperty("cert_path") + "/ca.crt";
        SSLContext sslContext;

        try (InputStream caInputStream = new FileInputStream(caCertPath)) {

            // Read CA certificate from file and convert it into X509Certificate
            CertificateFactory certFactory = CertificateFactory.getInstance("X509");
            X509Certificate caCert = (X509Certificate)certFactory.generateCertificate(caInputStream);

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            // Client Certificate no need to be provided in this use case, therefore no need to read the KeyStore
            ks.load(null);
            ks.setCertificateEntry("caCert", caCert);

            // Initialize Trust Manager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            // Create SSLContext. We need Trust Manager only in this use case
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        }

        return sslContext;
    }
}
