package jet.task.previewer.ftp;

/**
 * Indicates an error occurred on remote file stream retrieval.
 *
 * @see org.apache.commons.net.ftp.FTPClient#retrieveFileStream(String)
 */
public class FTPFileRetrievalException extends FTPCommandFailedException {
    public FTPFileRetrievalException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
