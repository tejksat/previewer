package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.StructureController;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Alex Koshevoy on 01.04.2015.
 */
public class FileStructureController extends PathStructureController {
    public static final String ZIP_FILE_EXTENSION = ".zip";
    public static final String ROOT_ZIP_FOLDER = "/";

    protected FileStructureController(@NotNull DefaultListModel<Path> targetListModel) {
        super(targetListModel);
    }

    @Override
    public boolean canBeEntered(@NotNull Path element) {
        return Files.isDirectory(element) || isZipFile(element);
    }

    @Override
    public Future<StructureController<?>> changeDirectory(@NotNull Path element,
                                                          @NotNull DoneCallback<StructureController<?>> doneCallback) {
        if (Files.isDirectory(element)) {
            SwingWorker<StructureController<?>, Void> worker = new ChangeDirectorySwingWorker(element, doneCallback);
            worker.execute();
            return worker;
        } else if (isZipFile(element)) {
            FileSystem fileSystem = FileSystems.getFileSystem(element.toUri());
            ZipStructureController zipStructureController = new ZipStructureController(getTargetListModel());
            return zipStructureController.changeDirectory(fileSystem.getPath(ROOT_ZIP_FOLDER), doneCallback);
        } else {
            throw new IllegalArgumentException("Cannot change directory to path " + element);
        }
    }

    private static boolean isZipFile(@NotNull Path element) {
        // todo may use isReadable()
        return Files.isRegularFile(element) && element.getFileName().toString().endsWith(ZIP_FILE_EXTENSION);
    }

    private class ChangeDirectorySwingWorker extends SwingWorker<StructureController<?>, Void> {
        private final Path path;
        private final DoneCallback<StructureController<?>> doneCallback;

        private volatile List<Path> directoryContent;

        public ChangeDirectorySwingWorker(@NotNull Path path, @NotNull DoneCallback<StructureController<?>> doneCallback) {
            this.path = path;
            this.doneCallback = doneCallback;
        }

        @Override
        protected StructureController<?> doInBackground() throws IOException {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
            listedElements = StreamSupport.stream(directoryStream.spliterator(), false).collect(Collectors.toList());
            return FileStructureController.this;
        }

        @Override
        protected void done() {
            doneCallback.done(this);
        }
    }
}
