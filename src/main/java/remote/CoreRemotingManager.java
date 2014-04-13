package remote;

import common.LocalProperties;
import ejb.AuthorizationBeanRemote;
import ejb.ContentBeanRemote;

import javax.naming.NamingException;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.13.4
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class CoreRemotingManager extends RemotingManager {

    public static String JBOSS_URL = LocalProperties.getProperties().getProperty("jboss.url");
    public static String JBOSS_LOGIN = LocalProperties.getProperties().getProperty("jboss.login");
    public static String JBOSS_PASSWORD = LocalProperties.getProperties().getProperty("jboss.password");

    public CoreRemotingManager() throws NamingException {
        super(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
    }
    public AuthorizationBeanRemote getAuthorizationBeanRemote() throws NamingException {
        Object bean = getContext().lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");

        return (AuthorizationBeanRemote)bean;
    }

    public ContentBeanRemote geContentBeanRemote() throws NamingException {
        Object bean  = getContext().lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");

        return (ContentBeanRemote)bean;
    }
}
