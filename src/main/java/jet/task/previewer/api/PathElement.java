package jet.task.previewer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Common class for {@link Path} based directory elements.
 */
public abstract class PathElement implements DirectoryElement {
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
    public <R> R consumeInputStream(InputStreamConsumer<R> consumer) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return consumer.accept(inputStream);
        }
    }

    @Override
    public String getName() {
        Path fileName = path.getFileName();
        return fileName != null ? fileName.toString() : path.toString();
    }
}
