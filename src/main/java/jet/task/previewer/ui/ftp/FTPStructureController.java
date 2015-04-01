package jet.task.previewer.ui.ftp;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.StructureController;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public class FTPStructureController implements StructureController<FTPFile> {
    // todo is it true? always?
    public static final String FTP_DIRECTORY_SEPARATOR = "/";

    private final FTPClient ftpClient;

    private String currentDirectory;


    public FTPStructureController(@NotNull FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public boolean changeToParentDirectory() throws IOException {
        return ftpClient.changeToParentDirectory();
    }

    @Override
    public Future<StructureController<?>> changeDirectory(@NotNull FTPFile directory, @NotNull DoneCallback doneCallback) throws IOException {
        // todo process symbolic links
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("File " + directory + " is not a directory");
        }
        // todo refactor
        String newCurrentDirectory = getChildFilePathname(directory);
        boolean result = ftpClient.changeWorkingDirectory(newCurrentDirectory);
        if (result) {
            currentDirectory = newCurrentDirectory;
            return null;
        } else {
            throw new RuntimeException(String.format("FTP server replied %d", ftpClient.getReplyCode()));
        }
    }

    @Override
    public InputStream getInputStream(@NotNull FTPFile element) throws IOException {
        return ftpClient.retrieveFileStream(element.getName());
    }

    public List<FTPFile> listFiles() throws IOException {
        return Arrays.asList(ftpClient.listFiles());
    }

    @Override
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public List<FTPFile> getListedElements() {
        // todo implement
        return null;
    }

    private String getChildFilePathname(FTPFile childFile) {
        return getCurrentDirectory() + FTP_DIRECTORY_SEPARATOR + childFile.getName();
    }

    @Override
    public boolean isDirectory(@NotNull FTPFile element) {
        return element.isDirectory();
    }

    @Override
    public boolean isFile(@NotNull FTPFile element) {
        return element.isFile();
    }

    @Override
    public boolean canBeEntered(@NotNull FTPFile element) {
        return isDirectory(element);
    }
}
