package auth;


import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.naming.directory.Attributes;
import java.util.*;

/**
 * Класс для маппинга полей из Active Directory в дополнительные поля пользователя
 */
public class LdapContextMapperImpl implements UserDetailsContextMapper {
    public UserDetails mapUserFromContext(DirContextOperations ctx,
                                          String username, Collection<? extends GrantedAuthority> authorities) {

        String email = "";
        String phone= "";
        String employeeId = "";
        String givenName = "";
        String displayName = "";

        Attributes attributes = ctx.getAttributes();
        try {
            employeeId = (String) attributes.get("employeeid").get();
            displayName = (String) attributes.get("displayname").get();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (javax.naming.NamingException e) {
            e.printStackTrace();
        }

        CustomUserDetails details = new CustomUserDetails(username, "", true, true, true, true, authorities, displayName, email, employeeId, phone);
        return details;
    }

    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {

    }
}
