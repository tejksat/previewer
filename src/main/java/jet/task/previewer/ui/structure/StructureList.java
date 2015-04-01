package jet.task.previewer.ui.structure;

import jet.task.previewer.model.Entry;
import jet.task.previewer.model.Leaf;
import jet.task.previewer.ui.EventUtils;
import jet.task.previewer.ui.engine.StructureController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
public class StructureList extends JList<Object> {
    private StructureController controller;

    public StructureList() {
        super(new DefaultListModel<Object>());
    }

    @Override
    public void setModel(ListModel<Object> model) {
        throw new UnsupportedOperationException(StructureList.class + " does not support arbitrary model change");
    }

    private void changeStructureSource(@NotNull StructureController controller, @NotNull ListModel model,
                                       @NotNull ListCellRenderer cellRenderer) {
        this.controller = controller;
        setModel(model);
        setCellRenderer(cellRenderer);
    }

    private void updateStructureSource() {

    }

    public static StructureList newInstance() {
        StructureList structureList = new StructureList();
        structureList.setCellRenderer(new StructureCellRenderer());
        structureList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (EventUtils.isPrimaryActionDoubleClick(e)) {
                    Optional<Integer> listIndex = EventUtils.getListIndexAtPoint(structureList, e.getPoint());
                    if (listIndex.isPresent()) {
                        Object element = structureList.getModel().getElementAt(listIndex.get());
                        changeDirectoryExt(structureList, element);
                    }
                }
            }
        });
        structureList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Object selectedElement = structureList.getSelectedValue();
                    changeDirectoryExt(structureList, selectedElement);
                }
            }
        });
        return structureList;
    }

    private static void changeDirectoryExt(StructureList structureList, Object selectedElement) {
        // todo REWRITE!!!! (check if folder and go deep)
        if (structureList.controller.isDirectory(selectedElement)) {
            StructureController newController = structureList.controller.changeDirectory(selectedElement);
            // todo commented because of partial commit
/*
            changeDirectory(structureList, selectedElement.getPath());
*/
        }/* else if (selectedElement instanceof Leaf) {
            // todo refactor
            Path path = selectedElement.getPath();
            if (path.toString().endsWith(".zip")) {
                try {
                    FileSystem fileSystem = FileSystems.newFileSystem(path, null);
                    changeDirectory(structureList, fileSystem.getPath("/"));
                } catch (IOException e) {
                    // todo log and show error message (status bar)
                    e.printStackTrace();
                }
            }
        }*/
    }

    private static void changeDirectory(StructureList structureList, Path path) {
        // todo should we disable list in another place (?)
        structureList.setEnabled(false);

        ListFolderSwingWorker swingWorker = new ListFolderSwingWorker(structureList, path);
        swingWorker.execute();
    }
}
