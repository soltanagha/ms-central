package ua.task.central.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumiditySensorHandler extends SensorDataHandler {
  Logger logger = LoggerFactory.getLogger(HumiditySensorHandler.class);
  private static final int HUMIDITY_THRESHOLD = 50;

  @Override
  public void handleSensorData(String key, String value) {
    if (!key.startsWith("h")) {
      passToNextHandler(key, value);
      return;
    }

    int humidity = Integer.parseInt(value);
    if (humidity >= HUMIDITY_THRESHOLD) {
      logger.warn("Humidity threshold exceeded! Sensor ID: {}, Value: {}", key, value);
    } else {
      logger.info("Normal humidity value. Sensor ID: {}, Value: {}", key, value);
    }
  }

}
