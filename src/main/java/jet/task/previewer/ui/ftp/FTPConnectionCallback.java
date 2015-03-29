package jet.task.previewer.ui.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Alex Koshevoy on 30.03.2015.
 */
public interface FTPConnectionCallback {
    void connectionEstablished(@NotNull FTPClient ftpClient);

    void connectionFailed();

    void loginFailed();
}
