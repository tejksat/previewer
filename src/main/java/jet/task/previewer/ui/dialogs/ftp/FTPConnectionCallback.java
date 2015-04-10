package jet.task.previewer.ui.dialogs.ftp;

import jet.task.previewer.ftp.FTPClientSession;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Alex Koshevoy on 30.03.2015.
 */
public interface FTPConnectionCallback {
    void connectionEstablished(@NotNull FTPClientSession ftpClient);

    void connectionFailed();

    void loginFailed();
}
