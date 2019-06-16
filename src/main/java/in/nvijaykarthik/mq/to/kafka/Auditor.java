package in.nvijaykarthik.mq.to.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class Auditor {

	
	private static final Logger log = LoggerFactory.getLogger(Auditor.class);

	public void saveInDb(Message<String> message) {
		log.info("Saving the message to the DB");
		
	}
}
