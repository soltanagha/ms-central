package ua.task.central.handler;

public abstract class SensorDataHandler {
  protected SensorDataHandler nextHandler;

  public void setNextHandler(SensorDataHandler nextHandler) {
    this.nextHandler = nextHandler;
  }

  public abstract void handleSensorData(String sensorId, String value);

  public void passToNextHandler(String key, String value) {
    if (nextHandler != null)
      nextHandler.handleSensorData(key, value);
  }

}
