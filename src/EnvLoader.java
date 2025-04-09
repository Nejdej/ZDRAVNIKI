package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("podatki.env"); // Load from project root
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.out.println("Error loading .env file: " + e.getMessage());
        }
    }

    public static String getEnv(String kajezelis) {
        return properties.getProperty(kajezelis);
    }
}