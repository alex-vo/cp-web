package controller;


import ejb.ContentBeanRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import remote.RemotingManager;
import structure.PlayList;
import structure.Song;

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
    public @ResponseBody List<Song> getPlayList(HttpSession httpSession,
                                                @RequestParam(value = "id", required = false) Long playListId) {

        RemotingManager remotingManager = null;
        PlayList playList = null;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            if(playListId == null){
                playList = bean.getPlayList((Long) httpSession.getAttribute("user"));
            }else{
                playList = bean.getPlayList((Long) httpSession.getAttribute("user"), playListId);
            }
            for(Song song : playList.getSongs()){
                logger.info("Song: " + song);
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
        return playList.getSongs();
    }

    @RequestMapping(value="/api/saveSongMetadata",
            method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public boolean saveSongMetadata(HttpSession httpSession, @RequestBody Song songObj) {

        RemotingManager remotingManager = null;

        boolean res = false;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");

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


    @RequestMapping("/api/getLink")
    public @ResponseBody String getFileLink(HttpSession httpSession,
                                            @RequestParam("cloud_id") Integer cloudId,
                                            @RequestParam("file_id") String fileId) {

        RemotingManager remotingManager = null;
        String fileLink = null;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            fileLink = bean.getFileSrc((Long) httpSession.getAttribute("user"), cloudId, fileId);
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

    @RequestMapping(value = "api/addPlayList", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public @ResponseBody long addPlayList(HttpSession httpSession, @RequestBody PlayList playList){
        RemotingManager remotingManager = null;
        long playListId = -1;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            playListId = bean.addPlayList((Long) httpSession.getAttribute("user"), playList);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return playListId;
    }

    @RequestMapping("api/getPlayLists")
    public @ResponseBody List<PlayList> getPlayLists(HttpSession httpSession){
        RemotingManager remotingManager = null;
        List<PlayList> result = null;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            result = bean.getPlayLists((Long)httpSession.getAttribute("user"));
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return result;
    }

    @RequestMapping("api/deletePlayList")
    public @ResponseBody boolean deletePlayList(HttpSession httpSession, @RequestParam("id") Long id){
        RemotingManager remotingManager = null;
        boolean result = false;
        try {
            remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            ContentBeanRemote bean = (ContentBeanRemote) context
                    .lookup("ejb:/cp-core//ContentBean!ejb.ContentBeanRemote");
            result = bean.deletePlayList(id);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return result;
    }
}
