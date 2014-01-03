package model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 12/27/13
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterFormModel {
    @Size(min = 5, message = "Please enter at least 5 characters")
    private String login;

    @Size(min = 5, message = "Please enter at least 5 characters")
    private String password;

    @Size(min = 5, message = "Please enter at least 5 characters")
    private String repeatPassword;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
