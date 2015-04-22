package jet.task.previewer.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Utility methods to work with {@link FTPClientUtils}.
 */
public class FTPClientUtils {
    public static final String FTP_ROOT_PATHNAME = "/";
    public static final String FTP_DIRECTORY_SEPARATOR = "/";

    private final static Logger logger = LoggerFactory.getLogger(FTPClientUtils.class);

    private FTPClientUtils() {
    }

    public static void disconnectQuietly(FTPClient ftpClient) {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            logger.debug("Error occurred on disconnect", e);
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
        return MessageFormat.format("FTP server reply code {0}, reply string {1}", ftpClient.getReplyCode(),
                formatFTPReplyString(ftpClient.getReplyString()));
    }

    public static String formatFTPReplyString(String replyString) {
        if (replyString == null) {
            return null;
        } else {
            return replyString.trim().replaceAll("\\n", "|");
        }
    }

    public static boolean hasParent(@NotNull String pathname) {
        pathname = pathname.trim();
        if (pathname.isEmpty()) {
            throw new IllegalArgumentException("pathname must not be empty");
        }
        pathname = removeEndSlashes(pathname);
        if (pathname.isEmpty()) {
            return false;
        }
        int lastSlash = pathname.lastIndexOf("/");
        return lastSlash != -1;
    }

    @NotNull
    public static String getParentPathname(@NotNull String pathname) {
        pathname = pathname.trim();
        if (pathname.isEmpty()) {
            throw new IllegalArgumentException("pathname must not be empty");
        }
        pathname = removeEndSlashes(pathname);
        if (pathname.isEmpty()) {
            throw new IllegalArgumentException("pathname denotes to root");
        }
        int lastSlash = pathname.lastIndexOf("/");
        if (lastSlash == -1) {
            throw new IllegalArgumentException("pathname has no parent");
        }
        pathname = pathname.substring(0, lastSlash);
        pathname = removeEndSlashes(pathname);
        if (pathname.isEmpty()) {
            return "/";
        } else {
            return pathname;
        }
    }

    @NotNull
    private static String removeEndSlashes(@NotNull String pathname) {
        int lastSlashIndex = pathname.length() - 1;
        while (lastSlashIndex >= 0 && pathname.charAt(lastSlashIndex) == '/') {
            lastSlashIndex--;
        }
        pathname = pathname.substring(0, lastSlashIndex + 1);
        return pathname;
    }

    public static String relativePath(@NotNull String pathname, @NotNull String filename) {
        if (pathname.endsWith(FTP_DIRECTORY_SEPARATOR)) {
            return pathname + filename;
        } else {
            return pathname + FTP_DIRECTORY_SEPARATOR + filename;
        }
    }
}
