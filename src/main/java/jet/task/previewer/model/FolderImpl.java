package jet.task.previewer.model;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Date;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class FolderImpl extends EntryImpl implements Folder {
    public FolderImpl(@NotNull Date created, @NotNull Date modified, long size, @NotNull Path path) {
        super(created, modified, size, path);
    }
}