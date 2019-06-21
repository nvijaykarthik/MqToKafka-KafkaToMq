package in.nvijaykarthik.mq.to.kafka;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(Auditor.class);
	
	@Autowired
	ExceptionRepo repo;

	public Message<String> saveInDb(Message<?> message) {
		
		log.info("Saving the message to the DB");
		MessagingException messagingException=null;
		String strMessagePayload=null;
		
		if (message.getPayload() instanceof MessagingException) {
			messagingException = (MessagingException) message.getPayload();
			strMessagePayload = (String) messagingException.getFailedMessage().getPayload();
		}
		MessageHeaders failedMsgHeaders = messagingException.getFailedMessage().getHeaders();
		
		String headers="";
		try {
			headers = new ObjectMapper().writeValueAsString(failedMsgHeaders);
		} catch (JsonProcessingException e) {
			log.error("Unable to process the headers from message");
		}
		
		String messageId=(String) failedMsgHeaders.get("messageId");
		String component=(String) failedMsgHeaders.get("component");
		
		ExceptionEntity EE= new ExceptionEntity();
		EE.setMessageId(messageId);
		EE.setComponent(component);
		EE.setPayload(strMessagePayload);
		EE.setHeaders(headers);
		EE.setExceptionMessage(messagingException.getMessage());
		EE.setStackTrace(ExceptionUtils.getStackTrace(messagingException));
		repo.save(EE);
		return MessageBuilder.withPayload(strMessagePayload).copyHeaders(failedMsgHeaders).setHeader("component", "EXCEPTION").build();
	}
	
	public void saveInLog(Message<?> message) {
		log.info("Saving the message to the Log");
		MessagingException messagingException=null;
		String strMessagePayload=null;
		if (message.getPayload() instanceof MessagingException) {
			messagingException = (MessagingException) message.getPayload();
		}
		log.error("Error while processing the exception",messagingException);
	}
}
