package in.nvijaykarthik.mq.to.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;

@Configuration
public class KafkaContainerConfiguration {

	@Autowired
	private KafkaAppProperties properties;
	
	@Autowired
	private KafkaProperties kafkaProperties;
	
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
		configurer.configure(factory, consumerFactory(kafkaProperties));
		return factory;
	}
	
	@Bean
	public ConsumerFactory<Object, Object> consumerFactory(KafkaProperties properties) {
		Map<String, Object> consumerProperties = properties.buildConsumerProperties();
		consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
		return new DefaultKafkaConsumerFactory<Object, Object>(consumerProperties);
	}

	//@Bean
	public KafkaMessageListenerContainer<Object, Object> container() {
		return new KafkaMessageListenerContainer<Object, Object>(consumerFactory(kafkaProperties),
				new ContainerProperties(new TopicPartitionInitialOffset(this.properties.getTopic(), 0)));
	}
		
	@Bean
	public KafkaMessageListenerContainer<Object, Object> kafkaAuditContainer() {
		return new KafkaMessageListenerContainer<Object, Object>(consumerFactory(kafkaProperties),
				new ContainerProperties(new TopicPartitionInitialOffset("auditTopic", 0)));
	}
	
	@Bean
	public KafkaMessageListenerContainer<Object, Object> kafkaExContainer() {
		Map<String, Object> consumerProps = kafkaProperties.buildProducerProperties();
		consumerProps.put("key.deserializer", StringDeserializer.class);
		consumerProps.put("value.deserializer", JavaDeSerializer.class);
		return new KafkaMessageListenerContainer<Object, Object>(new DefaultKafkaConsumerFactory<Object, Object>(consumerProps),
				new ContainerProperties(new TopicPartitionInitialOffset("exceptionTopic", 0)));
	}
	
	@Bean
	public KafkaMessageListenerContainer<Object, Object> kafkaApplicationContainer() {
		return new KafkaMessageListenerContainer<Object, Object>(consumerFactory(kafkaProperties),
				new ContainerProperties(new TopicPartitionInitialOffset("applicationTopic", 0)));
	}
	
	@Bean
	public KafkaMessageListenerContainer<Object, Object> kafkaOutputContainer() {
		return new KafkaMessageListenerContainer<Object, Object>(consumerFactory(kafkaProperties),
				new ContainerProperties(new TopicPartitionInitialOffset("outputToMq", 0)));
	}
}
