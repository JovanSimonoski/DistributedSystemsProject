import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class EventDeserializer implements Deserializer<Event> {

    @Override
    public Event deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Event) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
