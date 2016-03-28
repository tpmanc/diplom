package helpers;

import auth.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Вспомогательные функции для пользователя
 */
public class UserHelper {
    public static final String ADMIN_ROLE = "ROLE_FR-ADMIN";
    public static final String MODERATOR_ROLE = "ROLE_FR-MODERATOR";

    /**
     * Проверка прав админа
     */
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

    /**
     * Проверка прав модератора
     */
    public static boolean isModerator(CustomUserDetails activeUser) {
        Collection<GrantedAuthority> authorities = activeUser.getAuthorities();
        boolean hasRole = false;
        boolean isAdmin = UserHelper.isAdmin(activeUser);
        if (isAdmin) {
            return true;
        }
        for (GrantedAuthority authority : authorities) {
            hasRole = authority.getAuthority().equals(MODERATOR_ROLE);
            if (hasRole) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на аутентификацию
     */
    public static boolean isLogin() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            return true;
        }
        return false;
    }
}
