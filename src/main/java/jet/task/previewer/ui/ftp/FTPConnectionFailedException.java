package jet.task.previewer.ui.ftp;

/**
 * Created by Alex Koshevoy on 30.03.2015.
 */
public class FTPConnectionFailedException extends RuntimeException {
    private final int replyCode;
    private final String replyString;

    public FTPConnectionFailedException(int replyCode, String replyString) {
        this.replyCode = replyCode;
        this.replyString = replyString;
    }

    public int getReplyCode() {
        return replyCode;
    }

    public String getReplyString() {
        return replyString;
    }
}
