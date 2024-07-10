package ua.task.central.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemperatureSensorHandler extends SensorDataHandler {
  Logger logger = LoggerFactory.getLogger(HumiditySensorHandler.class);
  private static final int TEMPERATURE_THRESHOLD = 35;

  @Override
  public void handleSensorData(String key, String value) {
    if (!key.startsWith("t")) {
      passToNextHandler(key, value);
      return;
    }

    int temperature = Integer.parseInt(value);
    if (temperature >= TEMPERATURE_THRESHOLD) {
      logger.warn("Temperature threshold exceeded! Sensor ID: {}, Value: {}", key, value);
    } else {
      logger.info("Normal temperature value. Sensor ID: {}, Value: {}", key, value);
    }
  }

}
