package jet.task.previewer.ui.structure;

import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ui.engine.DirectoryElement;
import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.InputStreamConsumer;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.ftp.FTPResolver;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class FTPDirectoryElement implements DirectoryElement<FTPFile> {
    // todo is it true? always?
    public static final String FTP_DIRECTORY_SEPARATOR = "/";

    private final FTPClientSession ftpClient;
    private final String basePathname;
    private final FTPFile ftpFile;

    public FTPDirectoryElement(@NotNull FTPClientSession ftpClient,
                               @NotNull String basePathname,
                               @NotNull FTPFile ftpFile) {
        this.ftpClient = ftpClient;
        this.basePathname = basePathname;
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
        FTPResolver ftpResolver = new FTPResolver(ftpClient, basePathname + FTP_DIRECTORY_SEPARATOR + ftpFile.getName(), doneCallback);
        ftpResolver.execute();
        return ftpResolver;
    }

    @Override
    public <R> R consumeInputStream(@NotNull InputStreamConsumer<R> consumer) throws IOException {
        try {
            return ftpClient.consumeInputStream(ftpFile.getName(), consumer).get();
        } catch (InterruptedException e) {
            // todo ?
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            // todo ?
            throw new RuntimeException(e);
        }
/*
        InputStream inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
        if (inputStream == null) {
            System.err.println(MessageFormat.format("Failed to open input stream for file {0} (FTP reply code {1}, reply string {2})",
                    ftpFile.getName(), ftpClient.getReplyCode(), ftpClient.getReplyString()));

            int replyCode = ftpClient.getReplyCode();
            throw new RuntimeException("Input stream " + ftpFile.getName() + " cannot be opened (FTP server replied " + replyCode + ")");
        }
        System.out.println(MessageFormat.format("Input stream for file {0} opened (FTP reply code {1}, reply string {2})",
                ftpFile.getName(), ftpClient.getReplyCode(), ftpClient.getReplyString()));
        try {
            return consumer.accept(inputStream);
        } finally {
            try {
                closeQuietly(inputStream);
            } finally {
                if (ftpClient.completePendingCommand()) {
                    System.out.println("Command completed");
                } else {
                    // todo what do we need to do?
                    System.err.println("[TODO close connection?] Complete pending command failed");
                }
            }
        }
*/
    }

/*
    private static void closeQuietly(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Input stream close failed");
        }
    }
*/

    @Override
    public String getName() {
        return ftpFile.getName();
    }
}
