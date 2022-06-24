package eu.sharedtravel.app.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import eu.sharedtravel.app.config.properties.ApplicationProperties;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JWTConfig {

    private final ApplicationProperties properties;

    @Bean
    public JWTVerifier jwtVerifier()
        throws NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        return JWT.require(encodingAlgorithm())
            .withIssuer(properties.getJwt().getIssuer())
            .build();
    }

    @Bean
    public Algorithm encodingAlgorithm()
        throws NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        KeyPair keyPair = getKeyPair();

        return Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
    }

    private KeyPair getKeyPair()
        throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(properties.getJwt().getKeyStorePath());
        keyStore.load(input, properties.getJwt().getStorePassword().toCharArray());

        Key key = keyStore.getKey(properties.getJwt().getAlias(), properties.getJwt().getKeyPassword().toCharArray());
        if (!(key instanceof PrivateKey)) {
            log.error("Error occurred with key {}!", key);
            throw new IOException("Error occurred while extracting private key!");
        }
        // Get certificate of public key
        Certificate cert = keyStore.getCertificate(properties.getJwt().getAlias());

        // Get public key
        PublicKey publicKey = cert.getPublicKey();

        // Return a key pair
        return new KeyPair(publicKey, (PrivateKey) key);
    }
}
