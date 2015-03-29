package jet.task.previewer.ui.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jetbrains.annotations.NotNull;

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
            System.out.println("Connected to " + host + ".");
            System.out.print(ftpClient.getReplyString());

            String replyString = ftpClient.getReplyString();
            // After connection attempt, you should check the reply code to verify
            // success.
            replyCode = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(replyCode)) {
                disconnectQuietly(ftpClient);
                throw new FTPConnectionFailedException(replyCode, replyString);
/*
                System.err.println("FTP server refused connection.");
                System.exit(1);
*/
            }

            if (username != null) {
                if (!ftpClient.login(username, password)) {
                    disconnectQuietly(ftpClient);
                    throw new FTPLoginFailedException();
                }
            }

            // todo remove test delay
            Thread.sleep(2000L);

            // todo transfer files

/*
            ftp.logout();
*/
        } catch (IOException | FTPConnectionFailedException | FTPLoginFailedException e) {
            error = true;
            throw e;
        } finally {
            if (error && ftpClient.isConnected()) {
                // call function that prevent original exception from hiding
                disconnectQuietly(ftpClient);
            }
        }
        return ftpClient;
    }

    private void disconnectQuietly(FTPClient ftpClient) {
        try {
            ftpClient.disconnect();
        } catch (IOException ioe) {
            // do nothing
        }
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
