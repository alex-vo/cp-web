package common;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.7.4
 * Time: 23:44
 * To change this template use File | Settings | File Templates.
 */

@Startup
@Singleton
public class LocalProperties {

    private static Properties properties;

    @PostConstruct
    void atStartup() {
        properties = new Properties();

        try {
            properties.load(LocalProperties.class.getClassLoader().getResourceAsStream("local.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        if(properties == null){
            LocalProperties lp = new LocalProperties();
            lp.atStartup();
            return lp.getProperties();
        }
        return properties;
    }

}
