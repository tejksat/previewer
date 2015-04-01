package jet.task.previewer.ui.engine;

/**
 * Created by akoshevoy on 01.04.2015.
 */
public class ChangeDirectoryException extends RuntimeException {
    public ChangeDirectoryException(String message) {
        super(message);
    }

    public ChangeDirectoryException(Throwable cause) {
        super(cause);
    }
}
