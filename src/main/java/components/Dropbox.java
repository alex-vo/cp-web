package components;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;

import java.util.ArrayList;

/**
 * User: vanstr
 * Date: 13.29.6
 * Time: 20:55
 * Class represent basic Dropbox API functionality
 * Like: getFileList, getFileLink
 */
public class Dropbox {

    // Define application params
    final static private String APP_KEY = "uxw4eysrg39u7jw";
    final static private String APP_SECRET = "77p0nl292u8op2p";
    private static final Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    private static AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);

    private WebAuthSession session;
    private DropboxAPI<WebAuthSession> api;

    /**
     * Start session for execution API comands
     *
     * @param reAuthTokens
     */
    public Dropbox(AccessTokenPair reAuthTokens) {

        // start session
        session = new WebAuthSession(appKeys, ACCESS_TYPE);
        session.setAccessTokenPair(reAuthTokens);
        api = new DropboxAPI<WebAuthSession>(session);

    }


    // getAuthURL. generate link to dropbox, where user provides privileges to access his account data
    // getUserTokenPair, only after getAuthURL. Receive user tokens, after providing privileges

    /**
     * Get file link for downloading
     *
     * @param filePath
     * @return
     */
    public String getFileLink(String filePath) {

        String downloadLink = "";
        try {
            DropboxAPI.DropboxLink media = api.media(filePath, false);
            downloadLink = media.url;

            System.out.println("path:" + filePath);
            System.out.println("Link:" + downloadLink);
            System.out.println("Exp:" + media.expires);

        } catch (DropboxException e) {
            System.out.println("getFileLink: " + e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return downloadLink;
    }

    /**
     * @param folderPath - in which folder look up
     * @param recursion  - if true also include files from sub folders recusievly
     * @param fileType   - if file type == NULL return all list, ex: folder, files, mp3, txt
     *                   TODO, what if i want to get wav & mp3
     * @return TODO separate structure array{file_path,music metadata album song name, artist}
     *         How to get meta data of file?
     */
    public ArrayList<String> getFileList(String folderPath, boolean recursion, String fileType) {

        // Get folder content
        DropboxAPI.Entry dirent = null;
        try {
            dirent = api.metadata(folderPath, 1000, null, true, null);
        } catch (DropboxException e) {
            System.out.println(e);
        }

        // TODO create separate music list structure/class, should be similar in all clouds
        ArrayList<String> files = new ArrayList<String>();
        for (DropboxAPI.Entry ent : dirent.contents) {

            if (ent.isDir & recursion) {
                // start recursion through all folders
                System.out.println("Look in: " + ent.path);
                files.addAll(getFileList(ent.path, false, fileType));
            }

            // filter files by fileType -------------------------------->
            // TODO separete function, multiple file types MWA,MP3,avi...
            String fileName = ent.fileName().toLowerCase();
            int nameLength = fileName.length();
            String extension = fileName.substring(nameLength - 3, nameLength);
            /*
            System.out.println("nameL:" + nameLength );
            System.out.println("ext" + extension);
            System.out.println("fileTypes" + fileType);
            //*/
            if (extension.equals(fileType.toLowerCase())) {
                files.add(new String(ent.path));
            }
            // --------------------------------------------------------->

        }

        return files;
    }
}
