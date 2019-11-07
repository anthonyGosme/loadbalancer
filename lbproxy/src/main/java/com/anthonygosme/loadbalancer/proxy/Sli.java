package com.anthonygosme.loadbalancer.proxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sli {
  private static final Logger LOGGER = LogManager.getLogger(Sli.class);
  private long startTime;
  private long TotalTime;
  private int Code;
  private String host;

  Sli(HttpExchange req) {
    startTime = System.currentTimeMillis();
    host = req.getRequestURI().getHost();
  }

  void stop(int Code) {
    TotalTime = System.currentTimeMillis() - startTime;
    LOGGER.info("status " + Code + " - " + TotalTime + " ms");
    this.Code = Code;
  }
}
