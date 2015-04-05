package jet.task.previewer.ui.ftp;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.FTPDirectoryElement;
import jet.task.previewer.ui.structure.resolvers.ResolverSwingWorker;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class FTPResolver extends ResolverSwingWorker {
    private final FTPClient ftpClient;
    private final String pathname;

    public FTPResolver(@NotNull FTPClient ftpClient, @NotNull String pathname,
                       @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.ftpClient = ftpClient;
        this.pathname = pathname;
    }

    @Override
    protected FTPResolvedDirectory doInBackground() throws IOException {
        if (ftpClient.changeWorkingDirectory(pathname)) {
            // todo we may fail to list files
            FTPFile[] listedFiles = ftpClient.listFiles();
            List<FTPDirectoryElement> content = new ArrayList<>();
            for (FTPFile child : listedFiles) {
                if (child != null) { // we need this check (see FTPClient.listFiles() Javadoc)
                    content.add(new FTPDirectoryElement(ftpClient, pathname, child));
                }
            }
            return new FTPResolvedDirectory(pathname, content);
        } else {
            throw new RuntimeException("Failed to change current directory (FTP server reply code " + ftpClient.getReplyCode() + ")");
        }
    }
}
