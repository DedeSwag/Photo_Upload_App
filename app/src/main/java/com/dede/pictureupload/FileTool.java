package com.dede.pictureupload;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FileTool {
    /**
     * Description: Upload file to FTP server
     *
     * @param url
     *            FTP server hostname
     * @param port
     *            FTP server port
     * @param username
     *            FTP username for login
     * @param password
     *            FTP password for login
     * @param path
     *            The directories for FTP server saves, which are in the form of directories under Linux, such as/photo/
     * @param filename
     *            The file name uploaded defined by yourself to the FTP server.
     * @param input
     *            Input stream
     * @return If success, return true,otherwise return false.
     */
    public static boolean uploadFile(String url, int port, String username,
                                     String password, String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url, port);// connect
            ftp.login(username, password);//login
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.setFileType(FTP.BINARY_FILE_TYPE);//Upload the picture data format, or you won't be open on the server.
            if (!ftp.changeWorkingDirectory(path)) { //Create a new directory based on Path
                if (ftp.makeDirectory(path)) {
                    ftp.changeWorkingDirectory(path);
                }
            }
            ftp.enterLocalPassiveMode();
            if(!ftp.storeFile(filename, input)){
                return success;
            }
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
}
