package com.anthonygosme.loadbalancer;

import com.anthonygosme.loadbalancer.config.LoadingException;
import com.anthonygosme.loadbalancer.config.pojo.Host;
import com.anthonygosme.loadbalancer.proxy.DownStreamService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.anthonygosme.loadbalancer.proxy.DownStreamService.lookup_ServiceName_To_Service;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceMock {

  private static final List<Integer> launchedServicePort = new ArrayList<>();

  public static void callMockService(Host host) throws IOException {
    // create the request
    StringBuilder uri = new StringBuilder();
    uri.append("http://")
        .append(host.getAddress())
        .append(":")
        .append(host.getPort())
        .append("/test/");
    System.out.println("Calling " + uri);
    HttpGet request = new HttpGet(uri.toString());
    CloseableHttpClient httpClient = HttpClients.createDefault();
    // call the server abd check the response
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      System.out.println("checking HTTP/1.1 200 OK");
      assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
      HttpEntity entity = response.getEntity();
      String body = EntityUtils.toString(entity);
      System.out.println(body);
      EntityUtils.consume(entity);
    }
  }

  public Host startMockService() throws IOException, LoadingException {
    Host host = DownStreamService.getRandomHost(lookup_ServiceName_To_Service("my-service3"));
    if (launchedServicePort.contains(host.getPort())) {
      return host;
    }
    System.out.println("start startMockService on " + host.getPort());
    launchedServicePort.add(host.getPort());
    HttpServer server = HttpServer.create(new InetSocketAddress(host.getPort()), 0);
    System.out.println("service mock starting @ " + host.getPort());
    server.createContext("/", new MyHandler());
    server.setExecutor(null);
    server.start();
    return host;
  }

  static class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {

      StringBuilder response = new StringBuilder();
      response.append("the mock server is up \n host ").append(t.getLocalAddress());
      response.append("\nURI: ").append(t.getRequestURI());
      response.append("\nMethod: ").append(t.getRequestMethod());
      response.append("\nHeaders: ").append(t.getRequestHeaders().entrySet());
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.toString().getBytes());
      os.close();
    }
  }
}
