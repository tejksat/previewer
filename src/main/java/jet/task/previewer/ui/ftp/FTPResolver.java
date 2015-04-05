package jet.task.previewer.ui.ftp;

import jet.task.previewer.ftp.FTPClientManager;
import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.FTPDirectoryElement;
import jet.task.previewer.ui.structure.resolvers.ResolverSwingWorker;
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
public class FTPResolver extends ResolverSwingWorker {
    private final FTPClientManager ftpClient;
    private final String pathname;

    public FTPResolver(@NotNull FTPClientManager ftpClient, @NotNull String pathname,
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
        return new FTPResolvedDirectory(pathname, content);
    }
}
