package jet.task.previewer.ftp;

import java.text.MessageFormat;

/**
 * Created by Alex Koshevoy on 05.04.2015.
 */
public class FTPCommandFailedException extends Exception {
    public FTPCommandFailedException(int replyCode, String replyString) {
        super(MessageFormat.format("FTP server replied with code {0} ({1})", replyCode, replyString));
    }
}
