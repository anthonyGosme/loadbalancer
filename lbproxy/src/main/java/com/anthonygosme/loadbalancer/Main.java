package com.anthonygosme.loadbalancer;

import com.anthonygosme.loadbalancer.config.LoadingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.anthonygosme.loadbalancer.proxy.DownStreamService.loadConfig;
import static com.anthonygosme.loadbalancer.proxy.ProxyServer.startServer;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        int port = 8080;
        LOGGER.info("Proxy 4K8s is starting");

        try {
            loadConfig("/conf.d/proxy.yaml");
            startServer(port);
        } catch (LoadingException e) {
            LOGGER.error("Can't start the proxy on port: " + port, e);
        } catch (IOException e) {
            LOGGER.error("problem while executing retrieving the service: " + port, e);
        }
    }
}
