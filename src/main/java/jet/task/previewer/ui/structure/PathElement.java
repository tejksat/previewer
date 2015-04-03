package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public abstract class PathElement implements DirectoryElement<Path> {
    protected final Path path;

    protected PathElement(@NotNull Path path) {
        this.path = path;
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public boolean isFile() {
        return Files.isRegularFile(path);
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public String getName() {
        Path fileName = path.getFileName();
        return fileName != null ? fileName.toString() : path.toString();
    }
}
