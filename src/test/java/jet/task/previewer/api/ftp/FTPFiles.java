package jet.task.previewer.api.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

/**
 * Utility method for {@link FTPFile} structure.
 */
public class FTPFiles {
    private FTPFiles() {
    }

    public static FTPFile file(@NotNull String name) {
        return ftpFile(name, FTPFile.FILE_TYPE);
    }

    public static FTPFile directory(@NotNull String name) {
        return ftpFile(name, FTPFile.DIRECTORY_TYPE);
    }

    @NotNull
    private static FTPFile ftpFile(@NotNull String name, int fileType) {
        FTPFile ftpFile = new FTPFile();
        ftpFile.setName(name);
        ftpFile.setType(fileType);
        return ftpFile;
    }
}
