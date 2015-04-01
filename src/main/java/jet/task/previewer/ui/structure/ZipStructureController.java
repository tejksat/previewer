package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.StructureController;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListModel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public class ZipStructureController extends PathStructureController {
    protected ZipStructureController(@NotNull DefaultListModel<Path> targetListModel) {
        super(targetListModel);
    }

    @Override
    public boolean canBeEntered(@NotNull Path element) {
        return Files.isDirectory(element);
    }

    @Override
    public Future<StructureController<?>> changeDirectory(@NotNull Path element, @NotNull DoneCallback doneCallback) {
        return null;
    }
}
