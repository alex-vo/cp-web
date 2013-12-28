package model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 12/27/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginFormModel {
    @NotBlank(message = "Please provide login")
    private String login;

    @NotBlank(message = "Please provide password")
    private String password;

    private String successMessage;

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

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
