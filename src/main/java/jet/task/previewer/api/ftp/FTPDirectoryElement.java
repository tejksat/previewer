package jet.task.previewer.api.ftp;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.InputStreamConsumer;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.ftp.FTPClientSession;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * FTP file or directory.
 */
public class FTPDirectoryElement implements DirectoryElement {
    public static final String FTP_DIRECTORY_SEPARATOR = "/";

    private final FTPClientSession ftpClientSession;
    private final String pathname;
    private final FTPFile ftpFile;

    private final Logger logger = LoggerFactory.getLogger(FTPDirectoryElement.class);

    public FTPDirectoryElement(@NotNull FTPClientSession ftpClientSession,
                               @NotNull String pathname,
                               @NotNull FTPFile ftpFile) {
        this.ftpClientSession = ftpClientSession;
        this.pathname = pathname;
        this.ftpFile = ftpFile;
    }

    @Override
    public boolean isDirectory() {
        return ftpFile.isDirectory();
    }

    @Override
    public boolean isFile() {
        return ftpFile.isFile();
    }

    @Override
    public boolean canBeResolvedToDirectory() {
        return ftpFile.isDirectory();
    }

    @Override
    public Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        return FTPResolver.submit(ftpClientSession, pathname + FTP_DIRECTORY_SEPARATOR + ftpFile.getName(), doneCallback);
    }

    @Override
    public <R> R consumeInputStream(@NotNull InputStreamConsumer<R> consumer) throws IOException {
        try {
            return ftpClientSession.consumeInputStream(ftpFile.getName(), consumer).get();
        } catch (InterruptedException e) {
            logger.trace("Input stream consumption for [{}] has been interrupted", ftpFile.getName(), e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            logger.error("Input stream consumption for [{}] failed with exception", ftpFile.getName(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return ftpFile.getName();
    }
}
