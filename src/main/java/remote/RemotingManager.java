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

    public RemotingManager(String jbossURL, String jbossLogin, String jbossPassword) throws NamingException {
        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.PROVIDER_URL, jbossURL);
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, jbossLogin);
        jndiProperties.put(Context.SECURITY_CREDENTIALS, jbossPassword);
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put("jboss.naming.client.ejb.context", true);
        context = new InitialContext(jndiProperties);
    }

    public Context getContext() {
        return context;
    }

    public void terminate(){
        if(context != null){
            try {
                context.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finalize() throws Throwable {
        this.terminate();
        super.finalize();
    }
}
