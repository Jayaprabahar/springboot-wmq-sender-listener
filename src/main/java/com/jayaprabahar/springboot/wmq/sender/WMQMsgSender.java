package com.jayaprabahar.springboot.wmq.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayaprabahar.springboot.wmq.WMQConfigurationManager;

import lombok.extern.slf4j.Slf4j;

/**
 * <p> Project : com.jayaprabahar.springboot.wmq </p>
 * <p> Title : WMQMsgSender.java </p>
 * <p> Description: Message Sender bean class for WMQ </p>
 * <p> Created: Jun 5, 2018</p>
 * 
 * @version 1.0.0
 * @author <a href="mailto:jpofficial@gmail.com">Jayaprabahar</a>
 */
@Slf4j
@Component
public class WMQMsgSender {

	@Autowired
	private WMQConfigurationManager wMQConfigurationManager;

	/**
	 * Kind of test method used to send message to the Queue
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		log.info("Message sent : " + message);
		wMQConfigurationManager.getJmsTemplate().convertAndSend(message);
	}

}
