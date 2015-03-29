package jet.task.previewer.model;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Date;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public interface Entry {
    Date getCreated();

    Date getModified();

    long getSize();

    String getName();

    Path getPath();

    // todo for TEST only REMOVE
    class Factory {
        private Factory() {
        }

        public static Folder newFolder(@NotNull Path path) {
            return new FolderImpl(new Date(), new Date(), 239L, path);
        }

        public static Leaf newLeaf(@NotNull Path path) {
            return new LeafImpl(new Date(), new Date(), 239L, path);
        }
    }
}
