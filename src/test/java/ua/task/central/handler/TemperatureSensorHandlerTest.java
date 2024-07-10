package ua.task.central.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import static org.mockito.Mockito.*;

class TemperatureSensorHandlerTest {

  private TemperatureSensorHandler temperatureSensorHandler;

  @Mock
  private Logger mockLogger;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    temperatureSensorHandler = new TemperatureSensorHandler();
    temperatureSensorHandler.logger = mockLogger; // Assuming logger is protected or default visibility
  }

  @Test
  void handleSensorData_TemperatureBelowThreshold_LogsInfo() {
    String key = "t123";
    String value = "30";

    temperatureSensorHandler.handleSensorData(key, value);

    verify(mockLogger).info("Normal temperature value. Sensor ID: {}, Value: {}", key, value);
  }

  @Test
  void handleSensorData_TemperatureAboveThreshold_LogsWarn() {
    String key = "t123";
    String value = "40";

    temperatureSensorHandler.handleSensorData(key, value);

    verify(mockLogger).warn("Temperature threshold exceeded! Sensor ID: {}, Value: {}", key, value);
  }

  @Test
  void handleSensorData_KeyDoesNotStartWithT_PassesToNextHandler() {
    String key = "h123";
    String value = "20";

    SensorDataHandler nextHandler = mock(SensorDataHandler.class);
    temperatureSensorHandler.setNextHandler(nextHandler); // Assuming there is a setter for the next handler

    temperatureSensorHandler.handleSensorData(key, value);

    verify(nextHandler).handleSensorData(key, value);
  }
}