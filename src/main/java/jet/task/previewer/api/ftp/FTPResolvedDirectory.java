package jet.task.previewer.api.ftp;

import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ftp.FTPClientUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public class FTPResolvedDirectory implements ResolvedDirectory<FTPDirectoryElement> {
    private final FTPClientSession ftpClientSession;
    private final String currentPathname;
    private final List<FTPDirectoryElement> files;

    private final Logger logger = LoggerFactory.getLogger(FTPResolvedDirectory.class);

    public FTPResolvedDirectory(@NotNull FTPClientSession ftpClientSession,
                                @NotNull String currentPathname,
                                @NotNull List<FTPDirectoryElement> files) {
        this.ftpClientSession = ftpClientSession;
        this.currentPathname = currentPathname;
        this.files = files;
    }

    @Override
    public String getName() {
        return currentPathname;
    }

    @Override
    public List<FTPDirectoryElement> getContent() {
        return files;
    }

    @Override
    public boolean hasParent() {
        return FTPClientUtils.hasParent(currentPathname);
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        return FTPResolver.submit(ftpClientSession, FTPClientUtils.getParentPathname(currentPathname), doneCallback);
    }

    @Override
    public void dispose() {
        logger.debug("Closing FTP client session");
        ftpClientSession.close();
    }
}
