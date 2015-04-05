package jet.task.previewer.ftp;

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class FTPChangeDirectoryFailedException extends FTPCommandFailedException {
    public FTPChangeDirectoryFailedException(int replyCode, String replyString) {
        super(replyCode, replyString);
    }
}
