package jet.task.previewer.api.fs;

import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.InitiallyResolvedResolver;
import jet.task.previewer.api.PathResolvedDirectory;
import jet.task.previewer.api.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Resolved plain directory.
 */
public class FileResolvedDirectory extends PathResolvedDirectory<FileElement> {
    public static final String ZIP_FILE_EXTENSION = ".zip";
    public static final String ROOT_ZIP_FOLDER = "/";

    public FileResolvedDirectory(@NotNull Path currentPath,
                                 @NotNull List<FileElement> directoryContent) {
        super(currentPath, directoryContent);
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        Path parent = getCurrentPath().getParent();
        if (parent == null) {
            return InitiallyResolvedResolver.submit(new RootsResolvedDirectory(), doneCallback);
        } else {
            return FileDirectoryResolver.submit(parent, doneCallback);
        }
    }
}
