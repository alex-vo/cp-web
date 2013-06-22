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

    @Column(name="login", nullable = false)
    private String login;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="dropbox_token")
    private String dropboxToken;

    @Column(name="dropbox_secret")
    private String dropboxSecret;

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

    public String getDropboxToken() {
        return dropboxToken;
    }

    public void setDropboxToken(String dropboxToken) {
        this.dropboxToken = dropboxToken;
    }

    public String getDropboxSecret() {
        return dropboxSecret;
    }

    public void setDropboxSecret(String dropboxSecret) {
        this.dropboxSecret = dropboxSecret;
    }
}
