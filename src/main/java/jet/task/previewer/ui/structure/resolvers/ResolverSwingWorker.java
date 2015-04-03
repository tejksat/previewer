package jet.task.previewer.ui.structure.resolvers;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public abstract class ResolverSwingWorker extends SwingWorker<ResolvedDirectory<?>, Void> {
    protected final DoneCallback<ResolvedDirectory<?>> doneCallback;

    public ResolverSwingWorker(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        this.doneCallback = doneCallback;
    }
}
