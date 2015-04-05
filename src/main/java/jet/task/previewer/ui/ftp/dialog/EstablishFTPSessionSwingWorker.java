package jet.task.previewer.ui.ftp.dialog;

import jet.task.previewer.ui.ftp.FTPClientUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class EstablishFTPSessionSwingWorker extends SwingWorker<FTPClient, Void> {
    private final String host;
    private final Integer port;
    private String username;
    private String password;

    private final FTPConnectionCallback callback;

    private final Logger logger = LoggerFactory.getLogger(EstablishFTPSessionSwingWorker.class);

    public EstablishFTPSessionSwingWorker(@NotNull String host,
                                          @NotNull FTPConnectionCallback callback) {
        this.host = host;
        this.port = null;
        this.callback = callback;
    }

    public EstablishFTPSessionSwingWorker(@NotNull String host,
                                          @NotNull Integer port,
                                          @NotNull FTPConnectionCallback callback) {
        this.host = host;
        this.port = port;
        this.callback = callback;
    }

    public void setCredentials(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected FTPClient doInBackground() throws Exception {
        FTPClient ftpClient = new FTPClient();
        // todo ftp.configure()?
        boolean error = false;
        try {
            int replyCode;
            if (port != null) {
                ftpClient.connect(host, port);
            } else {
                // connect with default port
                ftpClient.connect(host);
            }

            logger.debug("Connected to {} ({})", host, ftpClient.getReplyString());

            String replyString = ftpClient.getReplyString();
            // After connection attempt, you should check the reply code to verify
            // success.
            replyCode = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.debug("Reply code is not positive {}, disconnecting from {}", ftpClient.getReplyCode(), host);

                FTPClientUtils.disconnectQuietly(ftpClient);
                throw new FTPConnectionFailedException(replyCode, replyString);
            }

            if (username != null) {
                if (!ftpClient.login(username, password)) {
                    FTPClientUtils.disconnectQuietly(ftpClient);
                    throw new FTPLoginFailedException();
                }
            }

            // todo remove test delay
            Thread.sleep(2000L);

            // todo transfer files
        } catch (IOException | FTPConnectionFailedException | FTPLoginFailedException e) {
            error = true;
            logger.error("Error has occurred while establishing connection to {}", host, e);
            throw e;
        } finally {
            if (error && ftpClient.isConnected()) {
                logger.info("Disconnecting from {}", host);
                FTPClientUtils.disconnectQuietly(ftpClient);
            }
        }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    @Override
    protected void done() {
        try {
            FTPClient ftpClient = get();
            callback.connectionEstablished(ftpClient);
        } catch (InterruptedException e) {
            e.printStackTrace();
            callback.connectionFailed();
        } catch (ExecutionException e) {
            e.printStackTrace();
            if (e.getCause() instanceof FTPConnectionFailedException) {
                callback.connectionFailed();
            } else if (e.getCause() instanceof FTPLoginFailedException) {
                callback.loginFailed();
            } else {
                callback.connectionFailed();
            }
        }
    }
}
