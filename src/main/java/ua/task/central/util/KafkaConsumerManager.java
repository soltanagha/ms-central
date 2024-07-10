package ua.task.central.util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.task.central.handler.HumiditySensorHandler;
import ua.task.central.handler.SensorDataHandler;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerManager {

  Logger logger = LoggerFactory.getLogger(HumiditySensorHandler.class);
  private static final String KAFKA_SERVER = "localhost:9092";
  private static final String TOPIC = "sensor_data";
  private final SensorDataHandler handler;
  private KafkaConsumer<String, String> consumer;
  private volatile boolean running = true;

  public KafkaConsumerManager(SensorDataHandler handler) {
    this.handler = handler;
  }

  public void startConsuming() {
    Properties props = setupKafkaProperties();
    try {
      consumer = new KafkaConsumer<>(props);
      consumer.subscribe(Collections.singletonList(TOPIC));
      while (running) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        processRecords(records);
      }
    } catch (Exception e) {
      logger.error("Failed in consuming messages: {}", e.getMessage());
      throw e;
    } finally {
      if (consumer != null) {
        consumer.close();
      }
    }
  }

  private void processRecords(ConsumerRecords<String, String> records) {
    for (ConsumerRecord<String, String> record : records) {
      logger.info("Processing record: {} => {}",record.key(), record.value());
      handler.handleSensorData(record.key(), record.value());
    }
  }

  public void stop() {
    this.running = false;
    if (consumer != null) {
      consumer.wakeup();  // Interrupts the consumer.poll() and throws a WakeupException
    }
  }

  private Properties setupKafkaProperties() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "sensor-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    return props;
  }
}
