package config;

import org.apache.log4j.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Листенер события старта приложения
 */
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext contextParent = contextRefreshedEvent.getApplicationContext().getParent();
        if (contextParent != null) {
            Properties props = new Properties();
            try {
                FileInputStream configStream = new FileInputStream("D:/log4j.properties");
                props.load(configStream);
                configStream.close();
            } catch(IOException e) {
                System.out.println("Error: Cannot laod configuration file ");
            }
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(props);
        }
    }
}
