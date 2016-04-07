package auth;

import exceptions.NotFoundException;
import models.UserModel;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import java.sql.SQLException;

/**
 * Реализация события AuthenticationSuccessEvent
 * Вызывается после успешного логина
 * Используется для получения доп полей из Active Directory
 */
public class UserService implements ApplicationListener<AuthenticationSuccessEvent> {
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        CustomUserDetails details = (CustomUserDetails) event.getAuthentication().getPrincipal();
        UserModel user;
        try {
            // при входе на сайте сохраняем информацию из AD в БД для текущего пользователя
            user = UserModel.findById(details.getEmployeeId());
            if (user == null) {
                user = new UserModel(
                        details.getEmployeeId(),
                        details.getPhone(),
                        details.getEmail(),
                        details.getFullname(),
                        details.getDepartment(),
                        details.getDepartmentNumber(),
                        details.getAddress()
                );
                user.add();
            } else {
                user.setEmail(details.getEmail());
                user.setPhone(details.getPhone());
                user.setDisplayName(details.getFullname());
                user.setAddress(details.getAddress());
                user.setDepartment(details.getDepartment());
                user.setDepartmentNumber(details.getDepartmentNumber());
                user.update();
            }
        } catch (SQLException e) {
            throw new NotFoundException("Ошибка БД при входе");
        }
    }
}
