package auth;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService implements ApplicationListener<AuthenticationSuccessEvent> {
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        System.out.println("LOGGGIGIGINNNN");
        CustomUserDetails sd = (CustomUserDetails) event.getAuthentication().getPrincipal();
        String dn = sd.getSid();
    }
}
