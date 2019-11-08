package com.anthonygosme.loadbalancer.proxy;

import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Sli {
  private static final Logger LOGGER = LogManager.getLogger(Sli.class);
  @Getter @Setter private long startTime;
  @Getter @Setter private long totalTime;
  @Getter @Setter private int code;
  @Getter @Setter private String host;

  Sli(HttpExchange req) {
    startTime = System.currentTimeMillis();
    host = req.getRequestURI().getHost();
  }

  void stop(int code) {
    totalTime = System.currentTimeMillis() - startTime;
    LOGGER.info("status " + code + " - " + totalTime + " ms");
    this.code = code;
  }
}
