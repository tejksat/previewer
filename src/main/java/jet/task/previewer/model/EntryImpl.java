package jet.task.previewer.model;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Date;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public abstract class EntryImpl implements Entry {
    private final Date created;
    private final Date modified;
    private final long size;
    private final Path path;

    public EntryImpl(@NotNull Date created,
                     @NotNull Date modified,
                     long size,
                     @NotNull Path path) {
        this.created = created;
        this.modified = modified;
        this.size = size;
        this.path = path;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getName() {
        return path.getNameCount() > 0 ? path.getName(path.getNameCount() - 1).toString() : path.toString();
    }

    @Override
    public Path getPath() {
        return path;
    }
}
