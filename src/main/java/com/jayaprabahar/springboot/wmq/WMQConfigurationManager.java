package com.jayaprabahar.springboot.wmq;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p> Project : com.jayaprabahar.springboot.wmq </p>
 * <p> Title : WMQConfigurationManager.java </p>
 * <p> Description: WMQ Configuration Manager. Renders configuration details from environment variables </p>
 * <p> Created: Jun 5, 2018</p>
 * 
 * @version 1.0.0
 * @author <a href="mailto:jpofficial@gmail.com">Jayaprabahar</a>
 */
@Configuration
@Slf4j
public class WMQConfigurationManager {
	
	@Getter
	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${WMQ_HOST_NAME}")
	private String wmqHostName;

	@Value("${WMQ_PORT_NUM}")
	private String wmqPortNum;

	@Value("${WMQ_QUEUE_MGR}")
	private String wmqQueueMgr;

	@Value("${WMQ_CHANNEL}")
	private String wmqChannel;

	@Value("${WMQ_QUEUE_NAME}")
	private String wmqQueueName;

	/**
	 * Creates MQQueueConnectionFactory instance based on the MQ configurations specific to the environment.
	 * 
	 * @return MQQueueConnectionFactory - Connection Factory for MQ
	 */
	public MQQueueConnectionFactory mqFactory() {
		MQQueueConnectionFactory factory = null;
		try {
			factory = new MQQueueConnectionFactory();
			factory.setHostName(wmqHostName);
			factory.setPort(Integer.parseInt(wmqPortNum));
			factory.setQueueManager(wmqQueueMgr);
			factory.setChannel(wmqChannel);
			factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		} catch (JMSException e) {
			log.error(e.getMessage());
		}
		return factory;
	}
	
	/**
	 * Sets default JMS listener container factory
	 * 
	 * @return JmsListenerContainerFactory - Default JmsListener Container Factory
	 */
	@SuppressWarnings("rawtypes")
	@Bean
	public JmsListenerContainerFactory jmsListenerContainerFactory() {
	    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	    factory.setConnectionFactory(mqFactory());
	    return factory;
	}

	/**
	 * Creates an JmsTemplate instance based for MQ configuration
	 */
	@PostConstruct
	public void init() {
		jmsTemplate = new JmsTemplate(mqFactory());
		jmsTemplate.setDefaultDestinationName(wmqQueueName);
	}
}