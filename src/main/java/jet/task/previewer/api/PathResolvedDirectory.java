package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public abstract class PathResolvedDirectory<E extends PathElement> implements ResolvedDirectory<E> {

    private final Path currentPath;

    private final List<E> directoryContent;

    protected PathResolvedDirectory(@NotNull Path currentPath,
                                    @NotNull List<E> directoryContent) {
        this.currentPath = currentPath;
        this.directoryContent = new ArrayList<>(directoryContent);
    }

    protected final Path getCurrentPath() {
        return currentPath;
    }

    @Override
    public String getName() {
        return currentPath.toString();
    }

    @Override
    public List<E> getContent() {
        return directoryContent;
    }

    @Override
    public void dispose() {
        // do nothing
    }
}
