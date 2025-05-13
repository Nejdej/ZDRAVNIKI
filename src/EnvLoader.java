package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("podatki.env")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
    }

    public static String getEnv(String key) {
        return properties.getProperty(key);
    }
}