package com.jayaprabahar.springboot.wmq.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * <p> Project : com.jayaprabahar.springboot.wmq </p>
 * <p> Title : WMQMsgListener.java </p>
 * <p> Description: Listener bean class for WMQ  </p>
 * <p> Created: Jun 5, 2018</p>
 * 
 * @version 1.0.0
 * @author <a href="mailto:jpofficial@gmail.com">Jayaprabahar</a>
 */
@Slf4j
@Component
public class WMQMsgListener {

	/**
	 * This listener method is the starting point where all the XML messages are received, parsed, and mail is sent to the concerned recipient.
	 * Exception is logged if there is a error in any of the aforementioned steps.
	 * 
	 * This method is triggered by the JMS MQ Listener
	 * 
	 * @param msg - String received from the MQ
	 */
	@JmsListener(destination = "${WMQ_QUEUE_NAME}")
	public void processMessage(String msg) {
		log.info("Received message from queue {}", msg);
	}

}
