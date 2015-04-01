package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.StructureController;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListModel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public abstract class PathStructureController implements StructureController<Path> {
    private final DefaultListModel<Path> targetListModel;

    private Path currentPath;

    protected List<Path> listedElements;

    protected PathStructureController(@NotNull DefaultListModel<Path> targetListModel) {
        this.targetListModel = targetListModel;
    }

    protected final DefaultListModel<Path> getTargetListModel() {
        return targetListModel;
    }

    protected final Path getCurrentPath() {
        return currentPath;
    }

    protected final void setCurrentPath(@NotNull Path currentPath) {
        this.currentPath = currentPath;
    }

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

    @Override
    public String getCurrentDirectory() {
        return currentPath.toString();
    }

    @Override
    public List<Path> getListedElements() {
        return listedElements;
    }
}
