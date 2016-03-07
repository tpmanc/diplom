package auth;


import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.Person;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.naming.directory.Attributes;
import java.util.*;

public class LdapContextMapperImpl implements UserDetailsContextMapper {
    public UserDetails mapUserFromContext(DirContextOperations ctx,
                                          String username, Collection<? extends GrantedAuthority> authorities) {

        String fullname = "";
        String email = "";
        String title = "";
        String sid = "";

        Attributes attributes = ctx.getAttributes();
        try {
            fullname = (String) attributes.get("displayName").get();
            sid = (String) attributes.get("objectcategory").get();
//            email = (String) attributes.get("mail").get();
//            title = (String) attributes.get("title").get();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (javax.naming.NamingException e) {
            e.printStackTrace();
        }

        CustomUserDetails details = new CustomUserDetails(username, "", true, true, true, true, authorities, fullname, email, title, sid);
        return details;
    }

    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {

    }

    public class CustomUserDetails extends User {

        private static final long serialVersionUID = 1416132138315457558L;

        // extra instance variables
        final String fullname;
        final String email;
        final String title;
        final String sid;

        public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                                 boolean credentialsNonExpired, boolean accountNonLocked,
                                 Collection<? extends GrantedAuthority> authorities, String fullname,
                                 String email, String title, String sid) {

            super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                    accountNonLocked, authorities);

            this.fullname = fullname;
            this.email = email;
            this.title = title;
            this.sid = sid;
        }

        public String getFullname() {
            return this.fullname;
        }

        public String getEmail() {
            return this.email;
        }

        public String getTitle() {
            return this.title;
        }

        public String getSid() {
            return this.sid;
        }
    }
}
