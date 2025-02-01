import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class ConsumerThreaded {

    public static void main(String[] args) {
        int numberOfThreads = 2;

        for (int i = 0; i < numberOfThreads; i++) {
            Thread producerThread = new Thread(new ConsumerTask("asia"));
            producerThread.start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            Thread producerThread = new Thread(new ConsumerTask("europe"));
            producerThread.start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            Thread producerThread = new Thread(new ConsumerTask("north-america"));
            producerThread.start();
        }
    }

    static class ConsumerTask implements Runnable {

        private final String continent;

        public ConsumerTask(String continent) {
            this.continent = continent;
        }

        @Override
        public void run() {
            String topicTemp = continent + "-temperature";
            String topicHum = continent + "-humidity";
            String topicAir = continent + "-airQuality";

            String bootstrapServers = getBootstrapServers(continent);
            Random random = new Random();

            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", EventDeserializer.class.getName());
            props.put("group.id", continent + "-consumer-group-" + random.nextInt(99999));
            props.put("auto.offset.reset", "earliest");

            KafkaConsumer<String, Event> consumer = new KafkaConsumer<>(props);

            consumer.subscribe(Arrays.asList(topicTemp, topicHum, topicAir));

            System.out.println("Subscribed to topics: " + topicTemp + ", " + topicHum + ", " + topicAir);

            double tempAverage = 0, humAverage = 0, airAverage = 0;
            boolean tempFirst = true, humFirst = true, airFirst = true;

            try {
                while (true) {
                    ConsumerRecords<String, Event> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, Event> record : records) {
                        System.out.println(record);
                        if (record.topic().endsWith("temperature")) {
                            if (tempFirst) {
                                tempFirst = false;
                                tempAverage = record.value().getValue();
                            } else {
                                tempAverage = 0.9 * tempAverage + 0.1 * record.value().getValue();
                            }
                        } else if (record.topic().endsWith("humidity")) {
                            if (humFirst) {
                                humFirst = false;
                                humAverage = record.value().getValue();
                            } else {
                                humAverage = 0.9 * humAverage + 0.1 * record.value().getValue();
                            }
                        } else if (record.topic().endsWith("airQuality")) {
                            if (airFirst) {
                                airFirst = false;
                                airAverage = record.value().getValue();
                            } else {
                                airAverage = 0.9 * airAverage + 0.1 * record.value().getValue();
                            }
                        }
                        System.out.printf("\n%s %s | Temperature: %.2f | Humidity: %.2f | Air Quality: %.2f |",
                                continent, Thread.currentThread().getName(), tempAverage, humAverage, airAverage);
                    }
                }
            } finally {
                consumer.close();
            }
        }
    }

    private static String getBootstrapServers(String continent) {
        String europeBootstrapServers = "10.0.0.101:9092,10.0.0.101:9093,10.0.0.103:9093"; // Europe 1 and 2 and Asia 2
        String asiaBootstrapServers = "10.0.0.103:9092,10.0.0.103:9093,10.0.0.102:9093"; // Asia 1 and 2 and North America 2
        String northAmericaBootstrapServers = "10.0.0.102:9092,10.0.0.102:9093,10.0.0.101:9093"; // North America 1 and 2 and Europe 2

        if (continent.equals("europe")) {
            return europeBootstrapServers;
        } else if (continent.equals("asia")) {
            return asiaBootstrapServers;
        }
        return northAmericaBootstrapServers;
    }
}
