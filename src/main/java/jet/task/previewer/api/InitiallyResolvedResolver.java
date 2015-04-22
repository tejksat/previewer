package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

/**
 * Resolver with initially resolved target.
 */
public class InitiallyResolvedResolver extends SwingWorkerResolver {
    private final ResolvedDirectory<?> resolvedDirectory;

    private InitiallyResolvedResolver(@NotNull ResolvedDirectory<?> resolvedDirectory,
                                      @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.resolvedDirectory = resolvedDirectory;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws Exception {
        return resolvedDirectory;
    }

    public static InitiallyResolvedResolver submit(@NotNull ResolvedDirectory<?> resolvedDirectory,
                                                   @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        InitiallyResolvedResolver worker = new InitiallyResolvedResolver(resolvedDirectory, doneCallback);
        worker.execute();
        return worker;
    }
}
