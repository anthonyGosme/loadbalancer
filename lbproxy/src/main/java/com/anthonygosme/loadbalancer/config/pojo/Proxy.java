package com.anthonygosme.loadbalancer.config.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"listen", "services"})
public class Proxy {

  @JsonProperty("listen")
  private Listen listen;

  @JsonProperty("services")
  private List<Service> services = null;

  @JsonProperty("listen")
  public Listen getListen() {
    return listen;
  }

  @JsonProperty("listen")
  public void setListen(Listen listen) {
    this.listen = listen;
  }

  @JsonProperty("services")
  public List<Service> getServices() {
    return services;
  }

  @JsonProperty("services")
  public void setServices(List<Service> services) {
    this.services = services;
  }
}
