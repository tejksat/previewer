package jet.task.previewer.ui.structure;

import jet.task.previewer.model.Entry;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingWorker;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class ListFolderSwingWorker extends SwingWorker<List<Entry>, List<Entry>> {
    private final StructureList structureList;
    private final Path path;

    public ListFolderSwingWorker(@NotNull StructureList structureList, @NotNull Path path) {
        this.structureList = structureList;
        this.path = path;
    }

    @Override
    protected List<Entry> doInBackground() throws Exception {
/*
        String[] pathElements = folder.getPath();
        Path path = FileSystems.getDefault().getPath(pathElements[0], Arrays.copyOfRange(pathElements, 1, pathElements.length));
*/
        // todo use stream api (?)
        DirectoryStream<Path> childPaths = Files.newDirectoryStream(path);
        // todo here we can process directory stream in chunks
        ArrayList<Entry> result = new ArrayList<>();
        if (path.getParent() != null) {
            result.add(Entry.Factory.newFolder(path.resolve("..")));
        }
        for (Path childPath : childPaths) {
            if (Files.isDirectory(childPath)) {
                result.add(Entry.Factory.newFolder(childPath));
            } else {
                result.add(Entry.Factory.newLeaf(childPath));
            }
        }
//        Thread.sleep(5000L);
        return result;
    }

    @Override
    protected void done() {
        try {
            if (isCancelled()) {
                // todo do something
            } else {
                try {
                    List<Entry> entries = get();
                    structureList.getModel().updateContent(entries);
                } catch (InterruptedException e1) {
                    // todo do something
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    // todo do something
                    e1.printStackTrace();
                }
            }
        } finally {
            structureList.setEnabled(true);
        }
    }
}
