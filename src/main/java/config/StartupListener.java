package config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ContextLoader;

/**
 * Листенер события старта приложения
 */
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
//            Settings settings = new Settings();
//            ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
//            IsFilled isFilled = (IsFilled) ctx.getBean("isFilled");
//            if (settings.isADFilled()) {
//                isFilled.setIsFilled(true);
//            }
        } catch (Exception e) {}
    }
}
