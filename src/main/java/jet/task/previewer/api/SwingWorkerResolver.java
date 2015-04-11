package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import javax.swing.SwingWorker;
import java.util.concurrent.Future;

/**
 * Common ancestor for all resolvers. Performs necessary actions in {@link SwingWorker#doInBackground()} and executes
 * {@link DoneCallback#done(Future)} callback method on {@link SwingWorker#done()} in Swing Dispatch Thread.
 */
public abstract class SwingWorkerResolver extends SwingWorker<ResolvedDirectory<?>, Void> {
    protected final DoneCallback<ResolvedDirectory<?>> doneCallback;

    protected SwingWorkerResolver(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        this.doneCallback = doneCallback;
    }

    @Override
    protected final void done() {
        doneCallback.done(this);
    }
}
