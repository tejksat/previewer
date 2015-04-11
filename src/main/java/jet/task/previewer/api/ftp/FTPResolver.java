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
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class FTPResolver extends SwingWorkerResolver {
    private final FTPClientSession ftpClient;
    private final String pathname;

    private FTPResolver(@NotNull FTPClientSession ftpClient, @NotNull String pathname,
                        @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.ftpClient = ftpClient;
        this.pathname = pathname;
    }

    @Override
    protected FTPResolvedDirectory doInBackground() throws IOException, ExecutionException, InterruptedException {
        Future<List<FTPFile>> result = ftpClient.changeWorkingDirectory(pathname);
        List<FTPFile> listedFiles = result.get();
        List<FTPDirectoryElement> content = listedFiles.stream()
                .filter(x -> x != null)
                .map(child -> new FTPDirectoryElement(ftpClient, pathname, child))
                .collect(Collectors.toList());
        // we need this check (see FTPClient.listFiles() Javadoc)
        return new FTPResolvedDirectory(ftpClient, pathname, content);
    }

    public static FTPResolver submit(@NotNull FTPClientSession ftpClient, @NotNull String pathname,
                                     @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        FTPResolver ftpResolver = new FTPResolver(ftpClient, pathname, doneCallback);
        ftpResolver.execute();
        return ftpResolver;
    }
}
