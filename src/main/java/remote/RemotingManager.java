package remote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/20/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemotingManager {
    private Context context;
    public RemotingManager() throws NamingException {
        //TODO use .properties
        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "alex");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "123");
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put("jboss.naming.client.ejb.context", true);
        context = new InitialContext(jndiProperties);
    }

    public Context getContext() {
        return context;
    }
}
