package com.anthonygosme.loadbalancer.proxy;

import com.anthonygosme.loadbalancer.config.LoadingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// https://www.programcreek.com/java-api-examples/?class=com.sun.net.httpserver.HttpExchange&method=getRequestHeaders
public class ProxyServerHandler implements HttpHandler {
  private static final Logger LOGGER = LogManager.getLogger(ProxyServerHandler.class);

  @Override
  public void handle(HttpExchange req) {
    try {
      Sli sli = new Sli(req);
      DownStreamService downStreamService = new DownStreamService();
      sli.stop(downStreamService.workFlow(req));
    } catch (Exception e) {
      LOGGER.error("error while loading the service: " + e.getMessage(), e);
    }
  }
}
