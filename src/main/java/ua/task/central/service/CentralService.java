package ua.task.central.service;

import ua.task.central.handler.HumiditySensorHandler;
import ua.task.central.handler.SensorDataHandler;
import ua.task.central.handler.TemperatureSensorHandler;
import ua.task.central.util.KafkaConsumerManager;

public class CentralService {
  private static CentralService instance;
  private final SensorDataHandler handler;
  private final KafkaConsumerManager consumerManager;

  private CentralService() {
    this.handler = initializeHandlers();
    this.consumerManager = new KafkaConsumerManager(handler);
  }

  public static synchronized CentralService getInstance() {
    if (instance == null) {
      instance = new CentralService();
    }
    return instance;
  }

  private SensorDataHandler initializeHandlers() {
    TemperatureSensorHandler temperatureHandler = new TemperatureSensorHandler();
    HumiditySensorHandler humidityHandler = new HumiditySensorHandler();
    humidityHandler.setNextHandler(temperatureHandler);
    return humidityHandler;
  }

  public void start() {
    consumerManager.startConsuming();
  }

  public void stop() {
    consumerManager.stop();
  }

}
