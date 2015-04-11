package jet.task.previewer.ftp;

import jet.task.previewer.api.InputStreamConsumer;
import jet.task.previewer.common.FileSystemUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulates work with {@link FTPClient}.
 * <p>
 * {@link FTPClient} is not thread-safe and we have to deal with it carefully to process incoming requests in a serial
 * way.
 */
public class FTPClientSession {
    private final FTPClient ftpClient;
    /**
     * Executor service for processing FTP commands in a serial way.
     */
    private final ExecutorService executorService;

    /**
     * Weak reference to a last consuming future, which we could cancel if new consumer is here.
     */
    private volatile WeakReference<Future<?>> lastConsumingFuture;

    private final Logger logger = LoggerFactory.getLogger(FTPClientSession.class);

    public FTPClientSession() {
        this.ftpClient = new FTPClient();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void connect(@NotNull String hostname, @NotNull Optional<Integer> port) throws IOException, FTPConnectionFailedException {
        if (ftpClient.isConnected()) {
            throw new IllegalStateException("Already connected to FTP server");
        }
        if (port.isPresent()) {
            ftpClient.connect(hostname, port.get());
        } else {
            ftpClient.connect(hostname);
        }
        // connected but we have not checked reply yet
        logger.debug("Connected to [{}]", hostname);
        int replyCode = ftpClient.getReplyCode();
        String replyString = ftpClient.getReplyString();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            if (replyString == null) {
                logger.warn("Connection to [{}] failed: server replied with code [{}]; disconnecting", hostname, replyCode);
            } else {
                logger.warn("Connection to [{}] failed: server replied with code [{}] and message [{}]; disconnecting", hostname, replyCode, replyString);
            }
            FTPClientUtils.disconnectQuietly(ftpClient);
            throw new FTPConnectionFailedException(replyCode, replyString);
        }
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            logger.error("Failed to change server file type to binary, disconnecting from {}", hostname);
            FTPClientUtils.disconnectQuietly(ftpClient);
            throw e;
        }
    }

    public void login(String username, String password) throws IOException, FTPLoginFailedException {
        if (!ftpClient.isConnected()) {
            throw new IllegalStateException("Not connected to server");
        }
        boolean result = ftpClient.login(username, password);
        if (!result) {
            throw new FTPLoginFailedException(ftpClient.getReplyCode(), ftpClient.getReplyString());
        }
    }

    @NotNull
    public Future<List<FTPFile>> changeWorkingDirectory(@NotNull String pathname) {
        return getListFuture(pathname, () -> ftpClient.changeWorkingDirectory(pathname));
    }

    @NotNull
    private Future<List<FTPFile>> getListFuture(@NotNull String pathname, Callable<Boolean> command) {
        logger.debug("Changing working directory to {}", pathname);
        return executorService.submit(() -> {
            try {
                if (command.call()) {
                    logger.debug("Working directory changed to {}", pathname);
                    // if FTPClient.listFiles() throws an exception it is unrecoverable
                    logger.debug("Listing files in directory {}", pathname);
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    logger.debug("Files in directory {} listed", pathname);
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
        });
    }

    @NotNull
    public <V> Future<V> consumeInputStream(String pathname, InputStreamConsumer<V> consume) {
        if (lastConsumingFuture != null) {
            Future<?> previousTask = lastConsumingFuture.get();
            if (previousTask != null) {
                previousTask.cancel(true);
                lastConsumingFuture = null;
            }
        }
        Future<V> future = executorService.submit(() -> {
            InputStream inputStream = ftpClient.retrieveFileStream(pathname);
            if (inputStream == null) {
                logger.warn("Unable to retrieve input stream for remote file {} ({})", pathname, FTPClientUtils.getServerReplyInformation(ftpClient));
                throw new FTPFileRetrievalException(ftpClient.getReplyCode(), ftpClient.getReplyString());
            } else {
                try {
                    return consume.accept(inputStream);
                } finally {
                    FileSystemUtils.closeQuietly(inputStream);
                    FTPClientUtils.completePendingCommandQuietly(ftpClient);
                }
            }
        });
        lastConsumingFuture = new WeakReference<>(future);
        return future;
    }

    public void close() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
                logger.error("Timeout occurred on shutting down {} executor service", FTPClientSession.class.getName());
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted on close", e);
        } finally {
            FTPClientUtils.disconnectQuietly(ftpClient);
        }
    }
}
