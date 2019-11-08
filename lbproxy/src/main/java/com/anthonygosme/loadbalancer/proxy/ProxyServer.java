package com.anthonygosme.loadbalancer.proxy;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ProxyServer {
    private static final Logger LOGGER = LogManager.getLogger(DownStreamService.class);

    public static void startServer(int port) throws IOException {
        LOGGER.info("starting the proxy listener on port: " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ProxyServerHandler());
        server.setExecutor(Executors.newFixedThreadPool(100));
        server.start();
        LOGGER.info("proxy listener started on port: " + port);
    }
}
