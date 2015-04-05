package jet.task.previewer.ui.ftp;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.FTPDirectoryElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public class FTPResolvedDirectory implements ResolvedDirectory<FTPDirectoryElement> {
    private final String currentPathname;
    private final List<FTPDirectoryElement> files;

    public FTPResolvedDirectory(@NotNull String currentPathname,
                                @NotNull List<FTPDirectoryElement> files) {
        this.currentPathname = currentPathname;
        this.files = files;
    }

/*
    @Override
    public Future<ResolvedDirectory<?>> changeDirectory(@NotNull FTPFile directory,
                                                        @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
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
*/

    @Override
    public String getCurrentDirectory() {
        return currentPathname;
    }

    @Override
    public List<FTPDirectoryElement> getDirectoryContent() {
        return files;
    }

    @Override
    public boolean hasParent() {
        // todo check if sometimes we DO have // as root pathname
        return !currentPathname.equals("/");
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        throw new RuntimeException("TODO implement");
    }

    /*
    @Override
    public boolean isDirectory(@NotNull FTPFile element) {
        return element.isDirectory();
    }

    @Override
    public boolean isFile(@NotNull FTPFile element) {
        return element.isFile();
    }

    @Override
    public boolean canBeResolvedToDirectory(@NotNull FTPFile element) {
        return isDirectory(element);
    }
*/
}
