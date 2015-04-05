package jet.task.previewer.ui.ftp;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class FTPClientUtils {
    private FTPClientUtils() {
    }

    public static void disconnectQuietly(FTPClient ftpClient) {
        try {
            ftpClient.disconnect();
        } catch (IOException ioe) {
            // do nothing
        }
    }

    public static void logoutQuietly(FTPClient ftpClient) {
        try {
            ftpClient.logout();
        } catch (IOException ioe) {
            disconnectQuietly(ftpClient);
        }
    }
}
