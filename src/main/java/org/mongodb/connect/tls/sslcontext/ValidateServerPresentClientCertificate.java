package org.mongodb.connect.tls.sslcontext;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Connect to a MongoDB cluster with TLS connection.
 * Validate MongoDB server's certificate with the CA certificate. Present a Client certificate to be validated by
 * the MongoDB server.
 *
 * Create a custom {@link javax.net.ssl.SSLContext} with the TrustStore holding the CA certificate and
 * the KeyStore holding the Client certificate and provide it to the MongoDB Driver.
 */
public class ValidateServerPresentClientCertificate {

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
                .applyToSslSettings(builder -> builder.context(sslContext))
                .build();

        MongoClient client = MongoClients.create(settings);

        MongoDatabase test = client.getDatabase("test");
        MongoCollection<Document> coll = test.getCollection("coll");

        // Retrieve the first document and print it
        System.out.println(coll.find().first());
    }

    /**
     * Load CA certificate from the file into the Trust Store.
     * Use PKCS12 keystore storing the Client certificate and read it into the {@link KeyStore}
     * Generate {@link SSLContext} from the Trust Store and {@link KeyStore}
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
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {

        String certsPath = System.getProperty("cert_path");

        // Path to the CA certificate on disk
        String caCertPath = certsPath + "/ca.crt";

        // Path to the PKCS12 Key Store holding the Client certificate
        String clientCertPath = certsPath + "/client.keystore.pkcs12";
        String clientCertPwd  = "123456";
        SSLContext sslContext;

        try (
                InputStream caInputStream = new FileInputStream(caCertPath);
                InputStream clientInputStream = new FileInputStream(clientCertPath)
        ) {
            // Read Client certificate from PKCS12 Key Store
            KeyStore clientKS = KeyStore.getInstance("PKCS12");
            clientKS.load(clientInputStream, clientCertPwd.toCharArray());

            // Retrieve Key Managers from the Client certificate Key Store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKS, clientCertPwd.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();

            // Read CA certificate from file and convert it into X509Certificate
            CertificateFactory certFactory = CertificateFactory.getInstance("X509");
            X509Certificate caCert = (X509Certificate)certFactory.generateCertificate(caInputStream);

            KeyStore caKS = KeyStore.getInstance(KeyStore.getDefaultType());
            caKS.load(null);
            caKS.setCertificateEntry("caCert", caCert);

            // Initialize Trust Manager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(caKS);

            // Create SSLContext. We need Trust Manager only in this use case
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, tmf.getTrustManagers(), null);
        }

        return sslContext;
    }
}
