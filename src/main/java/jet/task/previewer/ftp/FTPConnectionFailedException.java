package jet.task.previewer.ftp;

/**
 * Indicates that an error on connecting to FTP server occurred.
 *
 * @see org.apache.commons.net.ftp.FTPClient#connect(String)
 * @see org.apache.commons.net.ftp.FTPClient#connect(String, int)
 */
public class FTPConnectionFailedException extends FTPCommandFailedException {
    public FTPConnectionFailedException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
