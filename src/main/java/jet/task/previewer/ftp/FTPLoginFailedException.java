package jet.task.previewer.ftp;

/**
 * Created by Alex Koshevoy on 30.03.2015.
 */
public class FTPLoginFailedException extends FTPCommandFailedException {
    public FTPLoginFailedException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
