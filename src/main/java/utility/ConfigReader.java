package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigReader {
   private static final String CUSTOM_CONFIG_PATH = "src/main/resources/custom-config.properties";
   private static final Properties props = new Properties();

   static {
      try {
         File customFile = new File(CUSTOM_CONFIG_PATH);
         InputStream input = customFile.exists() ? new FileInputStream(customFile)
               : ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");

         if (input != null) {
            props.load(input);
            input.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private ConfigReader() {}

   public static String get(String key) {
      return props.getProperty(key);
   }

   public static void set(String key, String value) {
      props.setProperty(key, value);
      try (OutputStream output = new FileOutputStream(CUSTOM_CONFIG_PATH)) {
         props.store(output, null);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void deleteCustomConfig() {
      File file = new File(CUSTOM_CONFIG_PATH);
      if (file.exists()) file.delete();
   }

   public static void reload() {
      deleteCustomConfig();
      props.clear();
      try {
         File customFile = new File(CUSTOM_CONFIG_PATH);
         InputStream input = customFile.exists() ? new FileInputStream(customFile)
               : ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");

         if (input != null) {
            props.load(input);
            input.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
