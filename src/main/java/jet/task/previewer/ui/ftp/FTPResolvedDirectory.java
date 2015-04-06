package jet.task.previewer.ui.ftp;

import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.FTPDirectoryElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public class FTPResolvedDirectory implements ResolvedDirectory<FTPDirectoryElement> {
    private final FTPClientSession ftpClient;
    private final String currentPathname;
    private final List<FTPDirectoryElement> files;

    public FTPResolvedDirectory(@NotNull FTPClientSession ftpClient,
                                @NotNull String currentPathname,
                                @NotNull List<FTPDirectoryElement> files) {
        this.ftpClient = ftpClient;
        this.currentPathname = currentPathname;
        this.files = files;
    }

    @Override
    public String getCurrentDirectory() {
        return currentPathname;
    }

    @Override
    public List<FTPDirectoryElement> getDirectoryContent() {
        return files;
    }

    @Override
    public boolean hasParent() {
        return FTPClientUtils.hasParent(currentPathname);
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        FTPResolver ftpResolver = new FTPResolver(ftpClient, FTPClientUtils.getParentPathname(currentPathname), doneCallback);
        ftpResolver.execute();
        return ftpResolver;
    }
}
