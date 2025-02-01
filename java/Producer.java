import org.apache.kafka.clients.producer.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Random;

public class Producer {

    public static void main(String[] args) {
        int numberOfThreads = 2;

        for (int i = 0; i < numberOfThreads; i++) {
            Thread producerThread = new Thread(new ProducerTask("asia"));
            producerThread.start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            Thread producerThread = new Thread(new ProducerTask("europe"));
            producerThread.start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            Thread producerThread = new Thread(new ProducerTask("north-america"));
            producerThread.start();
        }
    }

    static class ProducerTask implements Runnable {
        private final String continent;

        public ProducerTask(String continent) {
            this.continent = continent;
        }

        @Override
        public void run() {
            String topicTemp = continent + "-temperature";
            String topicHum = continent + "-humidity";
            String topicAir = continent + "-airQuality";

            String bootstrapServers = getBootstrapServers(continent);

            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

            KafkaProducer<String, Event> producer = new KafkaProducer<>(props);
            Random random = new Random();

            EventType eventTypeTemp = EventType.valueOf("TEMPERATURE");
            EventType eventTypeHum = EventType.valueOf("HUMIDITY");
            EventType eventTypeAir = EventType.valueOf("AIR_QUALITY");

            int maxValueTemp = 40;
            int maxValueHum = 100;
            int maxValueAir = 500;

            String previousValueTemp = String.valueOf(random.nextInt(maxValueTemp));
            String previousValueHum = String.valueOf(random.nextInt(maxValueHum));
            String previousValueAir = String.valueOf(random.nextInt(maxValueAir));

            int counter = 0;
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000));

                    int nextValue;
                    float value;

                    // ======================================================================
                    nextValue = getNextValue(previousValueTemp, random, maxValueTemp);
                    value = (float) nextValue;
                    previousValueTemp = String.valueOf(nextValue);

                    Event eventTemp = new Event(eventTypeTemp, value, ZonedDateTime.now());
                    producer.send(new ProducerRecord<>(topicTemp, eventTemp));
                    System.out.printf("%s %s: %d Sent temp msg. - %s\n", continent, Thread.currentThread().getName(), counter, eventTemp);
                    // ======================================================================
                    nextValue = getNextValue(previousValueHum, random, maxValueHum);
                    value = (float) nextValue;
                    previousValueHum = String.valueOf(nextValue);

                    Event eventHum = new Event(eventTypeHum, value, ZonedDateTime.now());
                    producer.send(new ProducerRecord<>(topicHum, eventHum));
                    System.out.printf("%s %s: %d Sent hum msg. - %s\n", continent, Thread.currentThread().getName(), counter, eventHum);
                    // ======================================================================
                    nextValue = getNextValue(previousValueAir, random, maxValueAir);
                    value = (float) nextValue;
                    previousValueAir = String.valueOf(nextValue);

                    Event eventAir = new Event(eventTypeAir, value, ZonedDateTime.now());
                    producer.send(new ProducerRecord<>(topicAir, eventAir));
                    System.out.printf("%s %s: %d Sent air msg. - %s\n", continent, Thread.currentThread().getName(), counter, eventAir);

                    counter++;
                } catch (InterruptedException e) {
                    System.err.printf("Thread %s interrupted: %s%n", Thread.currentThread().getName(), e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        private static int getNextValue(String previousValue, Random random, int maxValue) {
            int delta = 1;
            int min, max, nextValue;

            min = Math.max(0, Integer.parseInt(previousValue) - delta);
            max = Math.min(maxValue, Integer.parseInt(previousValue) + delta);

            nextValue = random.nextInt(max - min + 1) + min;
            return nextValue;
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
}
