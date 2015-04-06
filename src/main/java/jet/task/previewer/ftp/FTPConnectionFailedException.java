package jet.task.previewer.ftp;

/**
 * Created by Alex Koshevoy on 30.03.2015.
 */
public class FTPConnectionFailedException extends FTPCommandFailedException {
    public FTPConnectionFailedException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
