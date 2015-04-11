package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class PreResolvedResolver extends SwingWorkerResolver {
    private final ResolvedDirectory<?> resolvedDirectory;

    private PreResolvedResolver(@NotNull ResolvedDirectory<?> resolvedDirectory,
                                @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.resolvedDirectory = resolvedDirectory;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws Exception {
        return resolvedDirectory;
    }

    public static PreResolvedResolver submit(@NotNull ResolvedDirectory<?> resolvedDirectory,
                                             @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        PreResolvedResolver worker = new PreResolvedResolver(resolvedDirectory, doneCallback);
        worker.execute();
        return worker;
    }
}
