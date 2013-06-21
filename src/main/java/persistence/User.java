package persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="login")
    private String login;

    @Column(name="password")
    private String password;

    public Long getId() {
        return id;
    }

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
}