package jet.task.previewer.ui.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class FTPClientUtils {
    private final static Logger logger = LoggerFactory.getLogger(FTPClientUtils.class);

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

    public static void completePendingCommandQuietly(FTPClient ftpClient) {
        try {
            if (ftpClient.completePendingCommand()) {
                logger.debug("Complete pending command done");
            } else {
                logger.info("Complete pending command failed ({})", getServerReplyInformation(ftpClient));
            }
        } catch (IOException e) {
            logger.warn("Complete pending command failed", e);
        }
    }

    public static String getServerReplyInformation(FTPClient ftpClient) {
        return MessageFormat.format("FTP server reply code {0}, reply string {1}", ftpClient.getReplyCode(), ftpClient.getReplyString());
    }
}
