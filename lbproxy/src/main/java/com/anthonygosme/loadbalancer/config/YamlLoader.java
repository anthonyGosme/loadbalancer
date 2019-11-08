package com.anthonygosme.loadbalancer.config;

import com.anthonygosme.loadbalancer.config.pojo.Config;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class YamlLoader {

  private static InputStream getStreamFromFile(String fileName) throws LoadingException {
    InputStream inputStream;
    try {
      File initialFile = new File(fileName);
      inputStream = new FileInputStream(initialFile);
    } catch (Exception err) {
      throw new LoadingException("can't find the proxy config file " + fileName, err);
    }
    //   if (inputStream == null)
    //     throw new LoadingException("no content loaded from the proxy config file " + fileName);
    return inputStream;
  }

  public static Map<String, Object> loadObjectFromFile(String fileName) throws LoadingException {
    Map<String, Object> obj;
    Yaml yaml = new Yaml();
    try {
      obj = yaml.load(getStreamFromFile(fileName));
    } catch (Exception err) {
      throw new LoadingException("can't parse Object from config content " + fileName, err);
    }
    return obj;
  }

  public static Config loadConfigFromFile(String fileName) throws LoadingException {
    Config config;
    Yaml yaml = new Yaml();
    try {
      config = yaml.loadAs(getStreamFromFile(fileName), Config.class);
    } catch (Exception err) {
      throw new LoadingException("can't parse Class config content " + fileName, err);
    }
    return config;
  }
}
