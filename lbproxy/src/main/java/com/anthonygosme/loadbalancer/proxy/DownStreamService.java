package com.anthonygosme.loadbalancer.proxy;

import com.anthonygosme.loadbalancer.config.LoadingException;
import com.anthonygosme.loadbalancer.config.pojo.Config;
import com.anthonygosme.loadbalancer.config.pojo.Host;
import com.anthonygosme.loadbalancer.config.pojo.Service;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.anthonygosme.loadbalancer.config.YamlLoader.loadConfigFromFile;

public class DownStreamService {
  private static final Logger LOGGER = LogManager.getLogger(DownStreamService.class);
  private static final String JAR_PATH = System.getProperty("user.dir");
  private static Config config;
  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  private CloseableHttpResponse httpResponse = null;

  public static Service lookupServiceNameToService(String lookupName) throws LoadingException {
    if (config == null) {
      LOGGER.error("config not loaded");
      return null;
    }
    for (Service service : config.getProxy().getServices()) {
      if (service.getName().equals(lookupName)) return service;
    }
    throw new LoadingException("Can't find the service with name:  " + lookupName);
  }

  private static Service lookupServiceDomainToService(String lookupDomain) throws LoadingException {
    for (Service service : config.getProxy().getServices()) {
      if (service.getDomain().equals(lookupDomain)) return service;
    }
    throw new LoadingException("Can't find the service with domain: " + lookupDomain);
  }

  public static Host getRandomHost(Service service) {
    if (service == null) return null;
    int randomId = new Random().nextInt(service.getHosts().size());
    return service.getHosts().get(randomId);
  }

  // ByteArrayOutputStream
  private static StringBuilder getbody(HttpExchange req) throws IOException {

    StringBuilder body = new StringBuilder();
    try (InputStreamReader reader = new InputStreamReader(req.getRequestBody(), Consts.UTF_8)) {
      char[] buffer = new char[256];
      int read;
      while ((read = reader.read(buffer)) != -1) {
        body.append(buffer, 0, read);
      }
      return body;
    }
  }

  public static void loadConfig(String proxyName) throws LoadingException {
    if (JAR_PATH == null) throw new LoadingException("the jar Path is null");
    LOGGER.error("execution path " + JAR_PATH);
    config = loadConfigFromFile(JAR_PATH + proxyName);
    LOGGER.info("YAML loaded");
  }

  int workFlow(HttpExchange requestUp) {
    ResponseObject responseO = new ResponseObject();
    try {
      Host hostDown = hostResolve(requestUp);
      Headers headerDownSun = getHeadersDown(requestUp, hostDown);
      Header[] headerDownApache = convertHeader(headerDownSun);
      StringBuilder body = getbody(requestUp);
      responseO =
          callTheService(
              hostDown,
              requestUp.getRequestURI().getRawPath(),
              headerDownApache,
              body.toString(),
              requestUp.getRequestMethod());
    } catch (LoadingException e) {
      LOGGER.error("error in service workflow: " + e.getMessage());
      responseO.body = e.getMessage();
      responseO.responseError = true;

    } catch (Exception e) {
      LOGGER.error("error in service workflow: ", e);
      responseO.body = ExceptionUtils.getStackTrace(e);
      responseO.responseError = true;
    }
    int code = setTheResponseUp(responseO, requestUp);
    requestUp.close();
    return code;
  }

  private void executeRequest(String type, String uri, String body, Header[] headerDown)
      throws LoadingException, IOException {

    HttpEntity entity = new ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8));
    LOGGER.info("calling service: " , uri);
    try {

      RequestConfig.Builder requestConfig = RequestConfig.custom();
      requestConfig.setConnectTimeout(5 * 1000);
      requestConfig.setConnectionRequestTimeout(5 * 1000);
      requestConfig.setSocketTimeout(5 * 1000);

      if (type.equals("POST")) {
        HttpPost request = new HttpPost(uri);
        request.setEntity(entity);
        request.setHeaders(headerDown);
        request.setConfig(requestConfig.build());
        httpResponse = httpClient.execute(request);
      }

      if (type.equals("PUT")) {
        HttpPut request = new HttpPut(uri);
        request.setEntity(entity);
        request.setHeaders(headerDown);
        request.setConfig(requestConfig.build());
        httpResponse = httpClient.execute(request);
      }

      if (type.equals("GET")) {
        HttpGet request = new HttpGet(uri);
        request.setHeaders(headerDown);
        request.setConfig(requestConfig.build());
        httpResponse = httpClient.execute(request);
      }

      if (type.equals("DELETE")) {
        HttpDelete request = new HttpDelete(uri);
        request.setHeaders(headerDown);
        request.setConfig(requestConfig.build());
        httpResponse = httpClient.execute(request);
      }
    } catch (Exception e) {

      httpClient.close();
      throw new LoadingException(e.getMessage());
    }
  }

  private ResponseObject callTheService(
      Host host, String servicePath, Header[] headerDown, String body, String method)
      throws IOException {
    String uri = "http://" + host.getAddress() + ":" + host.getPort() + servicePath;

    ResponseObject responseO = new ResponseObject();
    try {
      executeRequest(method, uri, body, headerDown);
    } catch (LoadingException e) {
      LOGGER.error("error calling the service: ", e);
      responseO.body = e.getMessage();

      responseO.responseError = true;

      return responseO;
    }
    if (httpResponse != null) responseO.body = EntityUtils.toString(httpResponse.getEntity());
    responseO.parameters = headerDown;
    if (httpResponse != null) responseO.statusLine = httpResponse.getStatusLine();
    if (httpResponse != null) httpResponse.close();
    return responseO;
  }

  private Header[] convertHeader(Headers headersSun) {
    Header[] headersApache = new Header[headersSun.size()];
    int i = 0;
    for (Map.Entry<String, List<String>> header : headersSun.entrySet()) {
      headersApache[i++] =
          new BasicHeader(header.getKey(), StringUtils.join(header.getValue(), ", "));
    }
    return headersApache;
  }

  private Headers getHeadersDown(HttpExchange requestUp, Host host) {
    Headers requestHeadersDown = new Headers();
    for (Map.Entry<String, List<String>> header : requestUp.getRequestHeaders().entrySet()) {
      if (header.getKey().equals("Host")) {
        List<String> values = new ArrayList<>();
        values.add(host.getAddress() + ":" + host.getPort());
        requestHeadersDown.put("Host", values);
      } else {

        if (!(requestUp.getRequestMethod().contains("P")
            && header.getKey().equals("Content-length")))
          requestHeadersDown.put(header.getKey(), header.getValue());
      }
    }
    return requestHeadersDown;
  }

  private Host hostResolve(HttpExchange requestUp) throws LoadingException {
    if (requestUp.getRequestHeaders() == null) throw new LoadingException("the request is null");
    if (requestUp.getRequestHeaders().getFirst("Host") == null)
      throw new LoadingException("no Host defined");
    String[] hostname = requestUp.getRequestHeaders().getFirst("Host").split(":");
    Service service = lookupServiceDomainToService(hostname[0]);
    return getRandomHost(service);
  }

  private int setTheResponseUp(ResponseObject response, HttpExchange requestUp) {
    try {
      int code;
      if (response.responseError) {
        code = 503;
        response.body = "lbproxy, " + response.body;
      } else {
        code = response.statusLine.getStatusCode();
      }

      if (response.body == null) response.body = "proxy : can't get any response from a service";
      requestUp.sendResponseHeaders(
          code, response.body.length() == 0 ? -1 : response.body.length());
      if (response.body.length() > 0) {
        try (OutputStream out = requestUp.getResponseBody()) {
          out.write(response.body.getBytes(Consts.UTF_8));
        }
      }
      return code;
    } catch (IOException e) {
      LOGGER.error("error setting the response: ", e);
      return 500;
    }
  }
}
