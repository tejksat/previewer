package jet.task.previewer.ftp;

/**
 * Indicates an error occurred on changing working directory of FTP server.
 *
 * @see org.apache.commons.net.ftp.FTPClient#changeWorkingDirectory(String)
 */
public class FTPChangeDirectoryFailedException extends FTPCommandFailedException {
    public FTPChangeDirectoryFailedException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
