package jet.task.previewer.ui.ftp;

import javafx.collections.ListChangeListener;
import jet.task.previewer.ui.engine.ChangeDirectoryException;
import jet.task.previewer.ui.engine.StructureController;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    public StructureController<?> changeDirectory(@NotNull FTPFile directory) {
        // todo process symbolic links
        if (!directory.isDirectory()) {
            throw new ChangeDirectoryException("File " + directory + " is not a directory");
        }
        // todo refactor
        String newCurrentDirectory = getChildFilePathname(directory);
        try {
            boolean result = ftpClient.changeWorkingDirectory(newCurrentDirectory);
            if (result) {
                currentDirectory = newCurrentDirectory;
                return this;
            } else {
                throw new ChangeDirectoryException(String.format("FTP server replied %d", ftpClient.getReplyCode()));
            }
        } catch (IOException e) {
            throw new ChangeDirectoryException(e);
        }
    }

    public List<FTPFile> listFiles() throws IOException {
        return Arrays.asList(ftpClient.listFiles());
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    private String getChildFilePathname(FTPFile childFile) {
        return getCurrentDirectory() + FTP_DIRECTORY_SEPARATOR + childFile.getName();
    }

    @Override
    public boolean isDirectory(@NotNull FTPFile element) {
        return element.isDirectory();
    }
}
