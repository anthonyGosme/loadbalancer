package com.anthonygosme.loadbalancer;

import com.anthonygosme.loadbalancer.config.LoadingException;
import com.anthonygosme.loadbalancer.proxy.DownStreamService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static com.anthonygosme.loadbalancer.ServiceMock.callMockService;
import static com.anthonygosme.loadbalancer.proxy.DownStreamService.lookupServiceNameToService;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProxyServiceTest {

  private final ServiceMock serviceMock = new ServiceMock();

  @BeforeAll
  static void loadConfGlobal() throws LoadingException {
    DownStreamService.loadConfig("./src/main/resources/target.yaml");
  }

  @Test
  void lookup_ServiceName_To_IdService() throws LoadingException {

    assertEquals(
        "my-service", Objects.requireNonNull(lookupServiceNameToService("my-service")).getName());
    assertEquals(
        "my-service2", Objects.requireNonNull(lookupServiceNameToService("my-service2")).getName());
  }

  @Test
  void getRandomHost() throws LoadingException {

    assertEquals(
        9090,
        Objects.requireNonNull(
                DownStreamService.getRandomHost(lookupServiceNameToService("my-service")))
            .getPort());

    assertEquals(
        9092,
        Objects.requireNonNull(
                DownStreamService.getRandomHost(lookupServiceNameToService("my-service2")))
            .getPort());
  }

  @Test
  void startMultipleProxy() throws IOException, LoadingException {
    for (int i = 0; i < 3; i++) {
      callMockService(serviceMock.startMockService());
    }
  }
}
