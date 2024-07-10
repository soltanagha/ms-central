package ua.task.central.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CentralServiceTest {

  @Test
  void testSingletonInstance() {
    CentralService firstInstance = CentralService.getInstance();
    CentralService secondInstance = CentralService.getInstance();
    assertSame(firstInstance, secondInstance, "Instances should be the same");
  }

}