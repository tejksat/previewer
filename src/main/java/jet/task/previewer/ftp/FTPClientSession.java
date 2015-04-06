package jet.task.previewer.ftp;

import jet.task.previewer.common.FileUtils;
import jet.task.previewer.ui.engine.InputStreamConsumer;
import jet.task.previewer.ui.ftp.FTPClientUtils;
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

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class FTPClientSession {
    private final FTPClient ftpClient;
    private final ExecutorService executorService;

    private volatile WeakReference<Future<?>> lastConsumingFuture;

    private final Logger logger = LoggerFactory.getLogger(FTPClientSession.class);

    public FTPClientSession() {
        this(new FTPClient());
    }

    @Deprecated
    public FTPClientSession(@NotNull FTPClient ftpClient) {
        this.ftpClient = ftpClient;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void connect(@NotNull String hostname, @NotNull Optional<Integer> port) throws IOException, FTPConnectionFailedException {
        if (ftpClient.isConnected()) {
            throw new IllegalStateException("Connected to FTP server");
        }
        if (port.isPresent()) {
            ftpClient.connect(hostname, port.get());
        } else {
            ftpClient.connect(hostname);
        }
        // connected but we have not checked reply yet
        logger.debug("Connected to {}", hostname);
        int replyCode = ftpClient.getReplyCode();
        String replyString = ftpClient.getReplyString();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            if (replyString == null) {
                logger.warn("Connection failed: server replied with code {}, disconnecting from {}", replyCode, hostname);
            } else {
                logger.warn("Connection failed: server replied with code {} and string {}, disconnecting from {}", replyCode, replyString, hostname);
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
    public Future<List<FTPFile>> changeToParentDirectory() {
        return getListFuture("parent", ftpClient::changeToParentDirectory);
    }

    @NotNull
    private Future<List<FTPFile>> getListFuture(@NotNull String pathname, Callable<Boolean> command) {
        logger.debug("Changing working directory to {}", pathname);
        return executorService.submit(new Callable<List<FTPFile>>() {
            @Override
            public List<FTPFile> call() throws Exception {
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
        Future<V> future = executorService.submit(new Callable<V>() {
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
        lastConsumingFuture = new WeakReference<>(future);
        return future;
    }

    public void disconnect() {
        // todo do we need to logout?
        FTPClientUtils.disconnectQuietly(ftpClient);
    }
}
