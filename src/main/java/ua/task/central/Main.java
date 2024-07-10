package ua.task.central;

import ua.task.central.service.CentralService;

public class Main {
  public static void main(String[] args) {
    CentralService centralService = CentralService.getInstance();
    centralService.start();
  }

}