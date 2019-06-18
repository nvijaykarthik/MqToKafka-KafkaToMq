package in.nvijaykarthik.mq.to.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ProcessMessage {

	
	private static final Logger log = LoggerFactory.getLogger(ProcessMessage.class);

	public Message<String> messageHandler(Message<String> msg) throws Exception {
        log.info("Received Mesage >>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+msg.getPayload());
        throw new Exception();
       // return msg;
    }
}
