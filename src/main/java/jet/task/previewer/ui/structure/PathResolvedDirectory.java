package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListModel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

/*
    @Override
    public boolean isDirectory(@NotNull Path element) {
        return Files.isDirectory(element);
    }

    @Override
    public boolean isFile(@NotNull Path element) {
        return Files.isRegularFile(element);
    }

    @Override
    public InputStream getInputStream(@NotNull Path element) throws IOException {
        return Files.newInputStream(element);
    }
*/

    @Override
    public String getCurrentDirectory() {
        return currentPath.toString();
    }

    @Override
    public List<E> getDirectoryContent() {
        return directoryContent;
    }
}
