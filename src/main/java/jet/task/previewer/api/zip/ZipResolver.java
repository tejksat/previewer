package jet.task.previewer.api.zip;

import jet.task.previewer.api.AbstractSwingResolver;
import jet.task.previewer.api.DirectoryResolverUtils;
import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.fs.FileResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class ZipResolver extends AbstractSwingResolver {
    private final Path element;

    private ZipResolver(@NotNull Path element,
                        @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        super(doneCallback);
        this.element = element;
    }

    @Override
    protected ResolvedDirectory<?> doInBackground() throws Exception {
        FileSystem fileSystem = FileSystems.newFileSystem(element, null);
        Path path = fileSystem.getPath(FileResolvedDirectory.ROOT_ZIP_FOLDER);
        return DirectoryResolverUtils.resolveZipDirectory(path, element);
    }

    public static ZipResolver submit(@NotNull Path path,
                                     @NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        ZipResolver worker = new ZipResolver(path, doneCallback);
        worker.execute();
        return worker;
    }
}
