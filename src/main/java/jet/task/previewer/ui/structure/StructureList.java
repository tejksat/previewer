package jet.task.previewer.ui.structure;

import jet.task.previewer.model.Entry;
import jet.task.previewer.model.Folder;
import jet.task.previewer.model.Leaf;
import jet.task.previewer.ui.EventUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.JList;
import javax.swing.ListModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureList extends JList<Entry> {
    public StructureList(@NotNull StructureListModel structureListModel) {
        super(structureListModel);
    }

    @Override
    public final void setModel(ListModel<Entry> model) {
        throw new UnsupportedOperationException(StructureList.class + " does not support a model change");
    }

    @Override
    public StructureListModel getModel() {
        // todo try to get rid of cast
        return (StructureListModel) super.getModel();
    }

    public static StructureList newInstance() {
        StructureListModel structureListModel = new StructureListModel();
        StructureList structureList = new StructureList(structureListModel);
        structureList.setCellRenderer(new StructureCellRenderer());
        structureList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (EventUtils.isPrimaryActionDoubleClick(e)) {
                    Optional<Integer> listIndex = EventUtils.getListIndexAtPoint(structureList, e.getPoint());
                    if (listIndex.isPresent()) {
                        Entry entry = structureListModel.getElementAt(listIndex.get());
                        changeDirectoryExt(structureList, entry);
                    }
                }
            }
        });
        structureList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Entry selectedEntry = structureList.getSelectedValue();
                    changeDirectoryExt(structureList, selectedEntry);
                }
            }
        });
        return structureList;
    }

    private static void changeDirectoryExt(StructureList structureList, Entry selectedEntry) {
        // todo REWRITE!!!! (check if folder and go deep)
        if (selectedEntry instanceof Folder) {
            changeDirectory(structureList, selectedEntry.getPath());
        } else if (selectedEntry instanceof Leaf) {
            // todo refactor
            Path path = selectedEntry.getPath();
            if (path.toString().endsWith(".zip")) {
                try {
                    FileSystem fileSystem = FileSystems.newFileSystem(path, null);
                    changeDirectory(structureList, fileSystem.getPath("/"));
                } catch (IOException e) {
                    // todo log and show error message (status bar)
                    e.printStackTrace();
                }
            }
        }
    }

    private static void changeDirectory(StructureList structureList, Path path) {
        // todo should we disable list in another place (?)
        structureList.setEnabled(false);

        ListFolderSwingWorker swingWorker = new ListFolderSwingWorker(structureList, path);
        swingWorker.execute();
    }
}
