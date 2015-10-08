package ru.atc.sbrf.ecmcore.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by vkoba on 13.08.2015.
 */
public class ConfigurationManager {
  private static Properties properties;

  static {
    properties = new Properties();
    try {
      if (properties.isEmpty()) {
        properties.load(ConfigurationManager.class.getClassLoader().getResourceAsStream("filenet_cfg.properties"));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getProperty(String key) {
    return properties.getProperty(key);
  }
}
