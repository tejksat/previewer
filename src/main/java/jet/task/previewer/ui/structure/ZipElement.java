package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.structure.resolvers.FileDirectoryResolverSwingWorker;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class ZipElement extends PathElement {
    public ZipElement(@NotNull Path path) {
        super(path);
    }

    @Override
    public boolean canBeResolvedToDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) {
        if (Files.isDirectory(path)) {
            return FileDirectoryResolverSwingWorker.executeNew(path, doneCallback);
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + path);
        }
    }
}
