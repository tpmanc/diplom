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
        String displayName = "";
        String department = "";
        String departmentNumber = "";
        String fax = "";
        String address = "";

        Attributes attributes = ctx.getAttributes();
        try {
            employeeId = (String) attributes.get("employeeid").get();
            if (attributes.get("displayname") != null) {
                displayName = (String) attributes.get("displayname").get();
            }
            if (attributes.get("telephoneNumber") != null) {
                phone = (String) attributes.get("telephoneNumber").get();
            }
            if (attributes.get("mail") != null) {
                email = (String) attributes.get("mail").get();
            }
            if (attributes.get("department") != null) {
                department = (String) attributes.get("department").get();
            }
            if (attributes.get("departmentNumber") != null) {
                departmentNumber = (String) attributes.get("departmentNumber").get();
            }
            if (attributes.get("facsimileTelephoneNumber") != null) {
                fax = (String) attributes.get("facsimileTelephoneNumber").get();
            }
            if (attributes.get("street") != null) {
                address = (String) attributes.get("street").get();
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (javax.naming.NamingException e) {
            e.printStackTrace();
        }

        CustomUserDetails details = new CustomUserDetails(username, "", true, true, true, true, authorities, displayName, email, employeeId, phone, department, departmentNumber, fax, address);
        return details;
    }

    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {

    }
}
