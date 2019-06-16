package in.nvijaykarthik.mq.to.kafka;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.config.EnableIntegration;

/**
 * Configuration class for jms and queues
 * 
 * @author Vijaykarthik N
 *
 */
@Configuration
@EnableIntegration
public class ActiveMQConfig {

	/** The generic application context. */
	@Autowired
	public GenericApplicationContext genericApplicationContext;

	/** The jms connection factory. */
	private static String JMS_CONNECTION_FACTORY = "jmsConnectionFactory";

	/** The connection factory. */
	private static String CONNECTION_FACTORY = "connectionFactory";

	/**
	 * Registers the bean named "jmsConnectionFactory" under the name
	 * "connectionFactory". This is needed because Spring boot creates the bean
	 * with name "jmsConnectionFactory", but Spring integration by default looks
	 * for a bean named "connectionFactory"
	 */
	@PostConstruct
	public void init() {
		genericApplicationContext.registerAlias(JMS_CONNECTION_FACTORY, CONNECTION_FACTORY);
	}
}