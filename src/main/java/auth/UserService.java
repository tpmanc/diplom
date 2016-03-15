package auth;

import exceptions.NotFoundException;
import models.UserModel;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import java.sql.SQLException;

/**
 * Реализация события AuthenticationSuccessEvent
 * Вызывается после успешного логина
 */
public class UserService implements ApplicationListener<AuthenticationSuccessEvent> {
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        CustomUserDetails details = (CustomUserDetails) event.getAuthentication().getPrincipal();
        UserModel user;
        try {
            user = UserModel.isExist(details.getEmployeeId());
            if (user == null) {
                user = new UserModel(details.getEmployeeId(), details.getPhone(), details.getEmail(), details.getFullname());
                user.add();
            }

        } catch (SQLException e) {
            throw new NotFoundException("Ошибка БД при входе");
        }
    }
}
