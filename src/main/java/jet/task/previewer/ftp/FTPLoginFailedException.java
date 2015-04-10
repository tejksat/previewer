package jet.task.previewer.ftp;

/**
 * Indicates an error on logging to FTP server.
 *
 * @see org.apache.commons.net.ftp.FTPClient#login(String, String)
 */
public class FTPLoginFailedException extends FTPCommandFailedException {
    public FTPLoginFailedException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
