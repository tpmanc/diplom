package helpers;

import auth.CustomUserDetails;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoader;

import java.util.Collection;

/**
 * Вспомогательные функции для пользователя
 */
public class UserHelper {
    public static String ADMIN_ROLE;
    public static String MODERATOR_ROLE;

    static {
        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        RolesHelper roles = (RolesHelper) ctx.getBean("rolesConfig");
        try {
            ADMIN_ROLE = "ROLE_" + roles.getAdmin().toUpperCase();
            MODERATOR_ROLE = "ROLE_" + roles.getModerator().toUpperCase();
        } catch (Exception e) {
            ADMIN_ROLE = "ROLE_ADMIN";
            MODERATOR_ROLE = "ROLE_MODERATOR";
        }
    }

    public static String checkBoth() {
        StringBuilder res = new StringBuilder();
        res
                .append("hasAnyRole('")
                .append(ADMIN_ROLE)
                .append("', '")
                .append(MODERATOR_ROLE)
                .append("')");
        return res.toString();
    }

    public static String checkAdmin() {
        StringBuilder res = new StringBuilder();
        res
                .append("hasRole('")
                .append(ADMIN_ROLE)
                .append("')");
        return res.toString();
    }

    public static String checkNoAdmin() {
        StringBuilder res = new StringBuilder();
        res
                .append("!hasRole('")
                .append(ADMIN_ROLE)
                .append("')");
        return res.toString();
    }

    public static String checkNoModeratorAndAdmin() {
        StringBuilder res = new StringBuilder();
        res
                .append("!hasRole('")
                .append(ADMIN_ROLE)
                .append("') && !hasRole('")
                .append(MODERATOR_ROLE)
                .append("')");
        return res.toString();
    }

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
