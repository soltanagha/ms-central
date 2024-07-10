package ua.task.central.handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import static org.mockito.Mockito.*;

class HumiditySensorHandlerTest {

  private HumiditySensorHandler humiditySensorHandler;

  @Mock
  private Logger mockLogger;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    humiditySensorHandler = new HumiditySensorHandler();
    humiditySensorHandler.logger = mockLogger; // Assuming logger is protected or default visibility
  }

  @Test
  void handleSensorData_HumidityBelowThreshold_LogsInfo() {
    String key = "h123";
    String value = "45";

    humiditySensorHandler.handleSensorData(key, value);

    verify(mockLogger).info("Normal humidity value. Sensor ID: {}, Value: {}", key, value);
  }

  @Test
  void handleSensorData_HumidityAboveThreshold_LogsWarn() {
    String key = "h123";
    String value = "55";

    humiditySensorHandler.handleSensorData(key, value);

    verify(mockLogger).warn("Humidity threshold exceeded! Sensor ID: {}, Value: {}", key, value);
  }

  @Test
  void handleSensorData_KeyDoesNotStartWithH_PassesToNextHandler() {
    String key = "t123";
    String value = "30";

    SensorDataHandler nextHandler = mock(SensorDataHandler.class);
    humiditySensorHandler.setNextHandler(nextHandler); // Assuming there is a setter for the next handler

    humiditySensorHandler.handleSensorData(key, value);

    verify(nextHandler).handleSensorData(key, value);
  }
}