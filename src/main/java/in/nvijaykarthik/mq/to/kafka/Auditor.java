package in.nvijaykarthik.mq.to.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Auditor {

	private static final Logger log = LoggerFactory.getLogger(Auditor.class);
	
	@Autowired
	AuditRepo repo;

	public void saveInDb(Message<String> message, @Header(name = "messageId", required = true) String messageId,
			@Header(name = "businessRefId", required = true) String businessRefId,
			@Header(name = "component", required = true) String component) {
		log.info("Saving the message to the DB");

		String payload=message.getPayload();
		String headers="";
		try {
			headers = new ObjectMapper().writeValueAsString(message.getHeaders());
		} catch (JsonProcessingException e) {
			log.error("Unable to process the headers from message");
		}
		AuditEntity AE= new AuditEntity();
		AE.setBussinessRefId(businessRefId);
		AE.setMessageId(messageId);
		AE.setComponent(component);
		AE.setPayload(payload);
		AE.setHeaders(headers);
		repo.save(AE);
		log.info("Message saved");
	}
}
