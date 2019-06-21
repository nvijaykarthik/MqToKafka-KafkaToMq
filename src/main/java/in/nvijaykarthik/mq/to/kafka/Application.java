/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.nvijaykarthik.mq.to.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.integration.kafka.config.xml.KafkaOutboundChannelAdapterParser;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * @author Vijaykarthik N
 * 
 */
@SpringBootApplication
@EnableConfigurationProperties(KafkaAppProperties.class)
@ImportResource({"classpath:common.xml","classpath:AuditAdaptor.xml","classpath:ExHandleAdaptor.xml","classpath:InputAdaptor.xml","classpath:OutputAdaptor.xml","classpath:ApplicationAdaptor.xml"})
@EnableIntegration
public class Application {

	@Autowired
	private KafkaAppProperties properties;
	
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);


	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(Application.class).run(args);
	}

	private void runDemo(ConfigurableApplicationContext context) {
		  MessageChannel toKafka = context.getBean("toKafka", MessageChannel.class);
		  System.out.println("Sending 10 messages..."); Map<String, Object> headers =
		  new HashMap<String, Object>(); headers.put(KafkaHeaders.TOPIC,
		  this.properties.getTopic()); for (int i = 0; i < 10; i++) { toKafka.send(new
		  GenericMessage<String>("foo" + i, headers)); }
		  System.out.println("Sending a null message...");
	}

	
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<String, String>(kafkaProducerFactory(kafkaProperties));
	}
	
	@Bean
	public KafkaTemplate<String, String> kafkaExTemplate() {
		Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
		producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		producerProperties.put("key.serializer", StringSerializer.class);
		producerProperties.put("value.serializer", JavaSerializer.class);
		return new KafkaTemplate<String, String>(new DefaultKafkaProducerFactory<String, String>(producerProperties));
	}
	
	
	
	@Bean
	public ProducerFactory<String, String> kafkaProducerFactory(KafkaProperties properties) {
		Map<String, Object> producerProperties = properties.buildProducerProperties();
		producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		return new DefaultKafkaProducerFactory<String, String>(producerProperties);
	}

  
	
	@Bean
	public MessageChannel fromKafka() {
		return new DirectChannel();
	}


	@Bean
	public NewTopic topic(KafkaAppProperties properties) {
		return new NewTopic(properties.getTopic(), 1, (short) 1);
	}

	@Bean
	public NewTopic newTopic(KafkaAppProperties properties) {
		return new NewTopic(properties.getNewTopic(), 1, (short) 1);
	}

	@Autowired
	private IntegrationFlowContext flowContext;

	@Autowired
	private KafkaProperties kafkaProperties;

	public void addAnotherListenerForTopics(String... topics) {
		Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
		// change the group id so we don't revoke the other partitions.
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG,
				consumerProperties.get(ConsumerConfig.GROUP_ID_CONFIG) + "x");
		IntegrationFlow flow = IntegrationFlows
				.from(Kafka.messageDrivenChannelAdapter(
						new DefaultKafkaConsumerFactory<String, String>(consumerProperties), topics))
				.channel("fromKafka").get();
		this.flowContext.registration(flow).register();
	}

}
