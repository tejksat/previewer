package jet.task.previewer.ftp;

import jet.task.previewer.common.FileUtils;
import jet.task.previewer.ui.engine.InputStreamConsumer;
import jet.task.previewer.ui.ftp.FTPClientUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class FTPClientManager {
    private final FTPClient ftpClient;
    private final ExecutorService executorService;

    private final Logger logger = LoggerFactory.getLogger(FTPClientManager.class);

    public FTPClientManager() {
        this(new FTPClient());
    }

    public FTPClientManager(@NotNull FTPClient ftpClient) {
        this.ftpClient = ftpClient;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public Future<List<FTPFile>> changeWorkingDirectory(@NotNull String pathname) {
        logger.debug("Changing working directory to {}", pathname);
        return executorService.submit(new Callable<List<FTPFile>>() {
            @Override
            public List<FTPFile> call() throws Exception {
                try {
                    if (ftpClient.changeWorkingDirectory(pathname)) {
                        logger.debug("Working directory changed to {}", pathname);
                        // if FTPClient.listFiles() throws an exception it is unrecoverable
                        FTPFile[] ftpFiles = ftpClient.listFiles();
                        return Arrays.asList(ftpFiles);
                    } else {
                        logger.warn("Unable to change working directory to {} ({})", pathname,
                                FTPClientUtils.getServerReplyInformation(ftpClient));
                        throw new FTPChangeDirectoryFailedException(ftpClient.getReplyCode(), ftpClient.getReplyString());
                    }
                } catch (IOException e) {
                    logger.error("Failed to change working directory to {}", pathname, e);
                    throw e;
                }
            }
        });
    }

    public <V> Future<V> consumeInputStream(String pathname, InputStreamConsumer<V> consume) {
        return executorService.submit(new Callable<V>() {
            @Override
            public V call() throws IOException, FTPFileRetrievalException {
                InputStream inputStream = ftpClient.retrieveFileStream(pathname);
                if (inputStream == null) {
                    logger.warn("Unable to retrieve input stream for remote file {} ({})", pathname, FTPClientUtils.getServerReplyInformation(ftpClient));
                    throw new FTPFileRetrievalException(ftpClient.getReplyCode(), ftpClient.getReplyString());
                } else {
                    try {
                        return consume.accept(inputStream);
                    } finally {
                        FileUtils.closeQuietly(inputStream);
                        FTPClientUtils.completePendingCommandQuietly(ftpClient);
                    }
                }
            }
        });
    }

/*
    public void connect() {

    }

    public void disconnect() {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            logger.debug("Failed to disconnect", e);
        }
    }
*/
}
