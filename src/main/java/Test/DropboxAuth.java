package Test;

import com.dropbox.client2.exception.*;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.net.URL;

public class DropboxAuth {

    final static private String APP_KEY = "uxw4eysrg39u7jw";
    final static private String APP_SECRET = "77p0nl292u8op2p";

    // Define AccessType for DropboxAPI object
    private static final AccessType ACCESS_TYPE = AccessType.DROPBOX;
    private static DropboxAPI<WebAuthSession> api;

    public static void main(String[] args) throws Exception {

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
        WebAuthInfo authInfo = session.getAuthInfo();

        RequestTokenPair pair = authInfo.requestTokenPair;

        String url = authInfo.url;
        Desktop.getDesktop().browse(new URL(url).toURI());
        JOptionPane.showMessageDialog(null, "Press ok to continue once you have authenticated.");
        session.retrieveWebAccessToken(pair);

        // Get token pair
        AccessTokenPair tokens = session.getAccessTokenPair();

        api = new DropboxAPI<WebAuthSession>(session);
        System.out.print("Uploading file...");
        String newLine = System.getProperty("line.separator");
        String fileContents = "Use this token pair in future so you don't have to re-authenticate each time: "  + newLine +
                " key:"+tokens.key  + newLine +
                " secret:" + tokens.secret;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContents.getBytes());


        try {
            //DropboxAPI.Entry existingEntry = api.metadata("/test.txt", 1, null, false, null);
            DropboxAPI.Entry newEntry = api.putFile("/test.txt", inputStream, fileContents.length(), null, null);
            System.out.println("Done. \nRevision of file: " + newEntry.rev);
        } catch (DropboxException e) { // exception example
            System.out.println("Something went wrong: " + e);
        }
    }
}