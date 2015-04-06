package jet.task.previewer.ui.ftp.dialog;

import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ftp.FTPConnectionFailedException;
import jet.task.previewer.ftp.FTPLoginFailedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 29.03.2015.
 */
public class EstablishFTPSessionSwingWorker extends SwingWorker<FTPClientSession, Void> {
    private final String hostname;
    private final Integer port;
    private String username;
    private String password;

    private final FTPConnectionCallback callback;

    private final Logger logger = LoggerFactory.getLogger(EstablishFTPSessionSwingWorker.class);

    public EstablishFTPSessionSwingWorker(@NotNull String hostname,
                                          @NotNull FTPConnectionCallback callback) {
        this.hostname = hostname;
        this.port = null;
        this.callback = callback;
    }

    public EstablishFTPSessionSwingWorker(@NotNull String hostname,
                                          @NotNull Integer port,
                                          @NotNull FTPConnectionCallback callback) {
        this.hostname = hostname;
        this.port = port;
        this.callback = callback;
    }

    public void setCredentials(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected FTPClientSession doInBackground() throws Exception {
        FTPClientSession ftpClient = new FTPClientSession();
        ftpClient.connect(hostname, Optional.ofNullable(port));
        try {
            ftpClient.login(username, password);
        } catch (IOException | FTPLoginFailedException e) {
            logger.warn("Disconnecting after failed attempt to login to {} with username {}", hostname, username);
            throw e;
        }
        return ftpClient;
    }

    @Override
    protected void done() {
        try {
            FTPClientSession ftpClient = get();
            callback.connectionEstablished(ftpClient);
        } catch (InterruptedException e) {
            logger.debug("Connection to {} has been interrupted", hostname, e);
            callback.connectionFailed();
        } catch (ExecutionException e) {
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
