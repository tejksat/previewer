package jet.task.previewer.ftp;

import jet.task.previewer.common.StringUtils;

import java.text.MessageFormat;

/**
 * Thrown if FTP server replied with negative reply code.
 *
 * @see org.apache.commons.net.ftp.FTPReply
 */
public class FTPCommandFailedException extends Exception {
    private static final String EMPTY_REPLY_STRING = "[empty]";

    /**
     * Creates {@link FTPCommandFailedException} with specified reply code and reply string.
     *
     * @param replyCode   FTP reply code
     * @param replyString FTP reply string
     */
    public FTPCommandFailedException(int replyCode, String replyString) {
        super(MessageFormat.format("FTP server replied with code {0} ({1})", replyCode,
                StringUtils.defaultIfEmpty(FTPClientUtils.formatFTPReplyString(replyString), EMPTY_REPLY_STRING)));
    }
}
