package com.jayaprabahar.springboot.wmq.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p> Project : com.jayaprabahar.springboot.wmq </p>
 * <p> Title : WMQMsgSender.java </p>
 * <p> Description: Async message sender class for WMQ </p>
 * <p> Created: Dec 7, 2022</p>
 *
 * @version 1.0.0
 * @author <a href="mailto:jpofficial@gmail.com">Jayaprabahar</a>
 */
@Slf4j
@Component
public class WMQMsgSender {

	private final JmsTemplate jmsTemplate;

	public WMQMsgSender(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * Send message to the Queue
	 * 
	 * @param message Message to be sent to MQ
	 */
	@Async
	public void sendMessage(String message) {
		log.info("Message sent : " + message);
		jmsTemplate.convertAndSend(message);
	}

}
