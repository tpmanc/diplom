package auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Класс для дополнительной информации по пользователю
 */
public class CustomUserDetails extends User {
    // дополнительные поля пользователя
    final String fullname;
    final String email;
    final String employeeId;
    final String phone;

    public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                             boolean credentialsNonExpired, boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities, String displayName,
                             String email, String employeeId, String phone) {

        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);

        this.fullname = displayName;
        this.email = email;
        this.employeeId = employeeId;
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getEmployeeId() {
        return Integer.parseInt(employeeId);
    }
}
