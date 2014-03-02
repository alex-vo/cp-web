package controller;

import com.google.gson.Gson;
import ejb.ContentBeanRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import remote.RemotingManager;
import structure.PlayList;
import structure.Song;
import structure.TrackList;

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

    final static Logger logger = LoggerFactory.getLogger(MusicController.class);

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

    @RequestMapping("/api/getPlayList")
    public @ResponseBody PlayList getPlayList(HttpSession httpSession) {

        RemotingManager remotingManager = null;
        PlayList playList = null;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            playList = bean.getPlayList((Long) httpSession.getAttribute("user"));
            for(Song song:playList){
                logger.info("Song:" +song);
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return playList;
    }

    @RequestMapping("/api/saveSongMetadata")
    public @ResponseBody boolean saveSongMetadata(HttpSession httpSession,
                                               @RequestParam(value = "jsonSongObject", required = true) String jsonSongObject) {

        RemotingManager remotingManager = null;

        boolean res = false;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");

            Gson gson = new Gson();

            logger.debug("JsonString parameter:" + jsonSongObject);
            //convert the json string back to object
            Song songObj = gson.fromJson(jsonSongObject, Song.class);

            logger.debug("Json converted java object:" + songObj);

            Long userId = (Long) httpSession.getAttribute("user");
            logger.info(songObj + "  " + userId);
            res = bean.saveSongMetadata(songObj, userId);


        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (remotingManager != null) {
                remotingManager.terminate();
            }
        }
        return res;
    }


    @RequestMapping("/api/getMusicList")
    public @ResponseBody TrackList getMusicList(HttpSession httpSession,
                               @RequestParam(value = "path", required = false) String path) {

        RemotingManager remotingManager = null;
        TrackList trackList = null;
        try {
            //TODO make static
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            List<String[]> fileList = bean.getFiles(path, (Long) httpSession.getAttribute("user"));
            trackList = new TrackList(fileList);
        } catch (NamingException ne) {
            if(trackList == null){
                trackList = new TrackList();
            }
            // TODO: Don't like that trackList also is responsible for message delivery
            trackList.setErrorMessage("Failed to connect the server");
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return trackList;
    }

    @RequestMapping("/api/getLink")
    public @ResponseBody String getFileLink(HttpSession httpSession,
                                            @RequestParam("path") String path,
                                            @RequestParam("cloud_id") Integer cloudId,
                                            @RequestParam("file_id") String fileId) {

        RemotingManager remotingManager = null;
        String fileLink = null;
        try {
            //TODO make static
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            fileLink = bean.getFileSrc(cloudId, path, (Long) httpSession.getAttribute("user"), fileId);
        } catch (NamingException ne) {
            ne.printStackTrace();
            fileLink = "error";
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return fileLink;
    }
}
