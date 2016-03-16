package helpers;

import auth.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Вспомогательные функции для пользователя
 */
public class UserHelper {
    public static final String ADMIN_ROLE = "ROLE_FR-ADMIN";

    public static boolean isAdmin(CustomUserDetails activeUser) {
        Collection<GrantedAuthority> authorities = activeUser.getAuthorities();
        boolean hasRole = false;
        for (GrantedAuthority authority : authorities) {
            hasRole = authority.getAuthority().equals(ADMIN_ROLE);
            if (hasRole) {
                return true;
            }
        }
        return false;
    }
}
