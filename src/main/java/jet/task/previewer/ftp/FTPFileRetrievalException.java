package jet.task.previewer.ftp;

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class FTPFileRetrievalException extends FTPCommandFailedException {
    public FTPFileRetrievalException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
