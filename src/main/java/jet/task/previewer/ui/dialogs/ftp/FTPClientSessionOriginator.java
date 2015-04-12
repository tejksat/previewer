package jet.task.previewer.ui.dialogs.ftp;

import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ftp.FTPConnectionFailedException;
import jet.task.previewer.ftp.FTPLoginFailedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Connecting to FTP server and logging in to establish new FTP session. Caller uses {@link FTPConnectionCallback}
 * to handle connection results.
 */
public class FTPClientSessionOriginator extends SwingWorker<FTPClientSession, Void> {
    private final String hostname;
    private final Integer port;
    private String username;
    private String password;

    private final FTPConnectionCallback callback;

    private final Logger logger = LoggerFactory.getLogger(FTPClientSessionOriginator.class);

    public FTPClientSessionOriginator(@NotNull String hostname,
                                      @NotNull FTPConnectionCallback callback) {
        this.hostname = hostname;
        this.port = null;
        this.callback = callback;
    }

    public FTPClientSessionOriginator(@NotNull String hostname,
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
        FTPClientSession ftpClientSession = new FTPClientSession();
        ftpClientSession.connect(hostname, Optional.ofNullable(port));
        try {
            ftpClientSession.login(username, password);
        } catch (IOException | FTPLoginFailedException e) {
            logger.warn("Disconnecting after failed attempt to login to [{}] with username [{}]", hostname, username);
            ftpClientSession.close();
            throw e;
        }
        return ftpClientSession;
    }

    @Override
    protected void done() {
        try {
            FTPClientSession ftpClientSession = get();
            callback.connectionEstablished(ftpClientSession);
        } catch (InterruptedException e) {
            logger.debug("Connection to [{}] has been interrupted", hostname, e);
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

    /**
     * Callback to be implemented.
     */
    public interface FTPConnectionCallback {
        /**
         * Called when connection successfully established.
         *
         * @param ftpClientSession {@link FTPClientSession} to be used
         */
        void connectionEstablished(@NotNull FTPClientSession ftpClientSession);

        /**
         * Called when connection failed (either IO exception occurred or FTP server replied with negative code).
         *
         * @see org.apache.commons.net.ftp.FTPClient#connect(String)
         * @see org.apache.commons.net.ftp.FTPClient#connect(String, int)
         */
        void connectionFailed();

        /**
         * Called when login failed.
         *
         * @see org.apache.commons.net.ftp.FTPClient#login(String, String)
         */
        void loginFailed();
    }
}
