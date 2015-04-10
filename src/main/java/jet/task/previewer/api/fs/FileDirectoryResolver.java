package jet.task.previewer.api.fs;

import jet.task.previewer.api.AbstractSwingResolver;
import jet.task.previewer.api.DirectoryResolverUtils;
import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class FileDirectoryResolver extends AbstractSwingResolver {
    private final Path path;

    private FileDirectoryResolver(@NotNull Path path, @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.path = path;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws IOException {
        return DirectoryResolverUtils.resolveFileDirectory(path);
    }

    public static FileDirectoryResolver submit(@NotNull Path path,
                                               @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        FileDirectoryResolver worker = new FileDirectoryResolver(path, doneCallback);
        worker.execute();
        return worker;
    }
}
