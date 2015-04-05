package jet.task.previewer.ui.structure.resolvers;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class PreResolvedResolverSwingWorker extends ResolverSwingWorker {
    private final ResolvedDirectory<?> resolvedDirectory;

    public PreResolvedResolverSwingWorker(@NotNull ResolvedDirectory<?> resolvedDirectory, @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.resolvedDirectory = resolvedDirectory;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws Exception {
        return resolvedDirectory;
    }
}
