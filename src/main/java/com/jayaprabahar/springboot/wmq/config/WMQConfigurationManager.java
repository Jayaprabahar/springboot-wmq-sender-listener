package com.jayaprabahar.springboot.wmq.config;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import javax.jms.JMSException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * <p> Project : com.jayaprabahar.springboot.wmq </p>
 * <p> Title : WMQConfigurationManager.java </p>
* <p> Description: WMQ Configuration Manager. Renders configuration details from environment variables </p>
 * <p> Created: Dec 7, 2022</p>
 *
 * @version 1.0.0
 * @author <a href="mailto:jpofficial@gmail.com">Jayaprabahar</a>
 */
@Configuration
@Slf4j
public class WMQConfigurationManager {

    @Value("${WMQ_HOST_NAME}")
    public String wmqHostName;

    @Value("${WMQ_PORT_NUM}")
    public String wmqPortNum;

    @Value("${WMQ_QUEUE_MGR}")
    public String wmqQueueMgr;

    @Value("${WMQ_CHANNEL}")
    public String wmqChannel;

    @Value("${WMQ_QUEUE_NAME}")
    public String wmqQueueName;

    @Value("${sslInstanceType:TLS}")
    public String sslInstanceType;

    @Value("${javax.net.ssl.keyStorePassword}")
    public String keyStorePass;

    @Value("${javax.net.ssl.keyStore}")
    Resource keyStoreFile;

    @Value("${javax.net.ssl.trustStorePassword}")
    public String trustStorePass;

    @Value("${javax.net.ssl.trustStore}")
    Resource trustStoreFile;

    @Value("${ibm.mq.cipher:}")
    private String cipher;

    /**
     * Sets default JMS listener container factory
     *
     * @return JmsListenerContainerFactory - Default JmsListener Container Factory
     */
    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory());
        return factory;
    }

    /**
     * Creates an JmsTemplate instance based for MQ configuration
     */
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setReceiveTimeout(20000);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setDefaultDestinationName(wmqQueueName);
        return jmsTemplate;
    }

    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(mqFactory());
        cachingConnectionFactory.setSessionCacheSize(500);
        cachingConnectionFactory.setCacheProducers(true);
        cachingConnectionFactory.setReconnectOnException(true);
        return cachingConnectionFactory;
    }

    public MQQueueConnectionFactory mqFactory() {
        MQQueueConnectionFactory factory = null;
        try {
            factory = new MQQueueConnectionFactory();
            factory.setHostName(wmqHostName);
            factory.setPort(Integer.parseInt(wmqPortNum));
            factory.setQueueManager(wmqQueueMgr);
            factory.setChannel(wmqChannel);
            factory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            factory.setCCSID(WMQConstants.CCSID_UTF8);
            factory.setSSLFipsRequired(false);
            factory.setSSLCipherSuite(cipher);
            factory.setSSLSocketFactory(getSocketFactory());

        } catch (JMSException e) {
            log.error(e.getMessage());
        }
        return factory;
    }

    private SSLSocketFactory getSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;

        try {
            KeyStore keyStore = loadKeyStoreJks();
            Enumeration<String> aliasEnumeration = keyStore.aliases();
            log.info("Alias {}", Collections.list(aliasEnumeration));

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePass.toCharArray());

            SSLContext sslContext = SSLContext.getInstance(sslInstanceType);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

            log.info("---------------- Supported Cipher Suites:- {} ", String.join(",", Arrays.asList(sslSocketFactory.getSupportedCipherSuites())));
            log.info("---------------- Default Cipher Suites:- {} ", String.join(",", Arrays.asList(sslSocketFactory.getDefaultCipherSuites())));

        } catch (UnrecoverableKeyException | CertificateException | KeyStoreException | KeyManagementException |
                 IOException | NoSuchAlgorithmException e) {
            log.error("Exception", e);
        }
        return sslSocketFactory;
    }

    @SuppressWarnings("unused")
    private KeyStore loadKeyStoreFromJre() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        String cacertName = System.getenv("JAVA_HOME") + "/lib/security/cacerts".replace("/", File.separator);
        log.info("cacertName:- {}", cacertName);

        KeyStore keystore;
        try (FileInputStream is = new FileInputStream(cacertName)) {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, keyStorePass.toCharArray());
        }

        return keystore;
    }

    private KeyStore loadKeyStoreJks() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(keyStoreFile.getInputStream(), keyStorePass.toCharArray());

        return keystore;
    }

}
