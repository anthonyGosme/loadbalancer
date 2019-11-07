package com.anthonygosme.loadbalancer;

import com.anthonygosme.loadbalancer.config.LoadingException;
import com.anthonygosme.loadbalancer.proxy.DownStreamService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.anthonygosme.loadbalancer.proxy.ProxyServer.startServer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
  private final ServiceMock serviceMock = new ServiceMock();

  @BeforeAll
  static void loadConfGlobal() {
    try {
      DownStreamService.loadConfig("./src/main/resources/target.yaml");
    } catch (LoadingException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  @Test
  void main() {
    try {
      for (int i = 0; i < 100; i++) {
        serviceMock.startMockService();
      }
      startServer(8080);
      HttpGet request = new HttpGet("http://my-service3.my-company.com:8080/tedq");
      CloseableHttpClient httpClient = HttpClients.createDefault();
      // call the server and check the response

      System.out.println("call the proxy: " + request.getURI());
      CloseableHttpResponse response = httpClient.execute(request);
      String bodyResponse = EntityUtils.toString(response.getEntity()) ;
      System.out.println(">" +bodyResponse);
      assertTrue(bodyResponse.contains("the mock server is up")) ;

    } catch (IOException | LoadingException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      assertTrue(false);
    }

  }
}
