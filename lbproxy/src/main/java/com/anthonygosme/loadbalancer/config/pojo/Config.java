package com.anthonygosme.loadbalancer.config.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"proxy"})
public class Config {

  @JsonProperty("proxy")
  private Proxy proxy;

  @JsonProperty("proxy")
  public Proxy getProxy() {
    return proxy;
  }

  @JsonProperty("proxy")
  public void setProxy(Proxy proxy) {
    this.proxy = proxy;
  }
}
