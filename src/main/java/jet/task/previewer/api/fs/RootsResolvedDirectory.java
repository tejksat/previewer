package jet.task.previewer.api.fs;

import jet.task.previewer.api.DoneCallback;
import jet.task.previewer.api.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * List of root directories.
 */
public class RootsResolvedDirectory implements ResolvedDirectory<FileElement> {
    private final List<FileElement> directoryContent;

    public RootsResolvedDirectory() {
        this.directoryContent = new ArrayList<>();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            directoryContent.add(new FileElement(path));
        }
    }

    @Override
    public String getName() {
        return "Root Directories";
    }

    @Override
    public List<FileElement> getContent() {
        return directoryContent;
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public Future<ResolvedDirectory<?>> resolveParent(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        throw new UnsupportedOperationException("Parent does not exist");
    }

    @Override
    public void dispose() {
        // do nothing
    }

    public static RootsResolvedDirectory newInstance() {
        return new RootsResolvedDirectory();
    }
}
