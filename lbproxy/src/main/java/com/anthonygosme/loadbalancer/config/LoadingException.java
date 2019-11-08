package com.anthonygosme.loadbalancer.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingException extends Exception {
  private static final Logger LOGGER = LogManager.getLogger(LoadingException.class);

  public LoadingException(String errorMessage, Throwable err) {
    super(errorMessage, err);
    LOGGER.error("catched error: " + getMessage());
  }

  public LoadingException(String errorMessage) {
    super(errorMessage);
  }
}
