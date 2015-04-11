package jet.task.previewer.api;

import javax.swing.SwingWorker;
import java.util.concurrent.Future;

/**
 * Callback that is to be executed in Swing Dispatch Thread (from {@link SwingWorker#done()} method) when {@link Future}
 * it has finished its work.
 */
public interface DoneCallback<E> {
    void done(Future<E> future);
}
