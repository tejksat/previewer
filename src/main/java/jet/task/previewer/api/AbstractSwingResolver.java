package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import javax.swing.SwingWorker;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public abstract class AbstractSwingResolver extends SwingWorker<ResolvedDirectory<?>, Void> {
    protected final DoneCallback<ResolvedDirectory<?>> doneCallback;

    protected AbstractSwingResolver(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        this.doneCallback = doneCallback;
    }

    @Override
    protected final void done() {
        doneCallback.done(this);
    }
}
