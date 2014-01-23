package controller;

import bo.TrackList;
import ejb.ContentBeanRemote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import remote.RemotingManager;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MusicController {

    @Value("#{localProperties['jboss.login']}")
    public String JBOSS_LOGIN;

    @Value("#{localProperties['jboss.password']}")
    public String JBOSS_PASSWORD;

    @Value("#{localProperties['jboss.url']}")
    public String JBOSS_URL;

    @RequestMapping("/app")
    public String app(ModelMap model){
        return "player";
    }

    @RequestMapping("/api/getMusicList")
    public @ResponseBody TrackList getMusicList(HttpSession httpSession,
                               @RequestParam(value = "path", required = false) String path) {
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            List<String[]> fileList = bean.getFiles(path, true, (Long) httpSession.getAttribute("user"));
            TrackList trackList = new TrackList(fileList);
            return trackList;
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/api/getLink")
    public @ResponseBody String getFileLink(HttpSession httpSession,
                                            @RequestParam("path") String path,
                                            @RequestParam("cloud_id") Integer cloudId,
                                            @RequestParam("file_id") String fileId) {
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            String fileLink = bean.getFileSrc(cloudId, path, (Long) httpSession.getAttribute("user"), fileId);
            return fileLink;
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
