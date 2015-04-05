package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
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
    public String getCurrentDirectory() {
        return "Root Directories";
    }

    @Override
    public List<FileElement> getDirectoryContent() {
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
}
