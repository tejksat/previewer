package jet.task.previewer.api.ftp;

import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.SwingWorkerResolver;
import jet.task.previewer.ftp.FTPClientSession;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Resolves FTP pathname within provided FTP client session as directory.
 */
public class FTPResolver extends SwingWorkerResolver {
    private final FTPClientSession ftpClientSession;
    private final String pathname;

    private FTPResolver(@NotNull FTPClientSession ftpClientSession, @NotNull String pathname,
                        @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.ftpClientSession = ftpClientSession;
        this.pathname = pathname;
    }

    @Override
    protected FTPResolvedDirectory doInBackground() throws IOException, ExecutionException, InterruptedException {
        Future<List<FTPFile>> result = ftpClientSession.changeWorkingDirectory(pathname);
        List<FTPFile> listedFiles;
        try {
            listedFiles = result.get();
        } catch (InterruptedException e) {
            result.cancel(true);
            throw e;
        }
        List<FTPDirectoryElement> content = listedFiles.stream()
                // we need this filter (see FTPClient.listFiles() Javadoc)
                .filter(x -> x != null)
                .map(child -> new FTPDirectoryElement(ftpClientSession, pathname, child))
                .collect(Collectors.toList());
        return new FTPResolvedDirectory(ftpClientSession, pathname, content);
    }

    public static FTPResolver submit(@NotNull FTPClientSession ftpClientSession, @NotNull String pathname,
                                     @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        FTPResolver ftpResolver = new FTPResolver(ftpClientSession, pathname, doneCallback);
        ftpResolver.execute();
        return ftpResolver;
    }
}
