package ua.task.central.util;

import static org.mockito.Mockito.*;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.task.central.handler.SensorDataHandler;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class KafkaConsumerManagerTest {

  @Mock
  private SensorDataHandler handler;

  @Mock
  private KafkaConsumer<String, String> kafkaConsumer;

  private KafkaConsumerManager kafkaConsumerManager;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    kafkaConsumerManager = new KafkaConsumerManager(handler);

    // Use reflection to set the private 'consumer' field
    Field consumerField = KafkaConsumerManager.class.getDeclaredField("consumer");
    consumerField.setAccessible(true);
    consumerField.set(kafkaConsumerManager, kafkaConsumer);
  }

  @Test
  void testStartConsuming() throws Exception {
    doNothing().when(kafkaConsumer).subscribe(Collections.singletonList("sensor_data"));
    when(kafkaConsumer.poll(Duration.ofMillis(1000))).thenReturn(new ConsumerRecords<>(Collections.emptyMap()));

    // Use a CountDownLatch to control the flow of the test
    CountDownLatch latch = new CountDownLatch(1);
    doAnswer(invocation -> {
      latch.countDown();
      return new ConsumerRecords<>(Collections.emptyMap());
    }).when(kafkaConsumer).poll(any(Duration.class));

    // Use a separate thread to start consuming and then stop it after a short delay
    Thread consumerThread = new Thread(() -> kafkaConsumerManager.startConsuming());
    consumerThread.start();

    latch.await(2, TimeUnit.SECONDS);
    kafkaConsumerManager.stop();
    consumerThread.join();

    verify(kafkaConsumer, never()).poll(Duration.ofMillis(1000));
    verify(handler, never()).handleSensorData(anyString(), anyString());
  }

  @Test
  void testStopConsuming() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    doAnswer(invocation -> {
      latch.countDown();
      return new ConsumerRecords<>(Collections.emptyMap());
    }).when(kafkaConsumer).poll(any(Duration.class));

    Thread consumerThread = new Thread(() -> kafkaConsumerManager.startConsuming());
    consumerThread.start();

    latch.await(2, TimeUnit.SECONDS);

    kafkaConsumerManager.stop();
    consumerThread.join();

    verify(kafkaConsumer, never()).wakeup();
  }
}