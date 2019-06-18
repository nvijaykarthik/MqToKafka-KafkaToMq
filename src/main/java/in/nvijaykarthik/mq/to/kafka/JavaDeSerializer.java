package in.nvijaykarthik.mq.to.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

public class JavaDeSerializer implements Deserializer<Object> {

	@Override
	public Object deserialize(String topic, byte[] data) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(in);
			return is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalStateException("Can't serialize object: " + data, e);
		}
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	@Override
	public void close() {

	}

}
