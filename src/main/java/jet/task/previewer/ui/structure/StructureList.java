package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.EventUtils;
import jet.task.previewer.ui.engine.DirectoryElement;
import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureList extends JList<DirectoryElement> {
    public StructureList() {
        super(new StructureListModel());
    }

    @Override
    public void setModel(ListModel<DirectoryElement> model) {
        throw new UnsupportedOperationException(StructureList.class + " does not support arbitrary model change");
    }

    public StructureListModel getThisModel() {
        return (StructureListModel) getModel();
    }

    public void setCurrentDirectory(@NotNull ResolvedDirectory<?> currentDirectory) {
        StructureListModel model = getThisModel();
        model.setCurrentDirectory(currentDirectory);
        requestFocus();
        if (!model.isEmpty()) {
            setSelectedIndex(0);
        }
    }

    public ResolvedDirectory<?> getCurrentDirectory() {
        return getThisModel().getCurrentDirectory();
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
                        DirectoryElement element = structureList.getThisModel().getElementAt(listIndex.get());
                        changeDirectoryExt(structureList, element);
                    }
                }
            }
        });
        structureList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DirectoryElement selectedElement = structureList.getSelectedValue();
                    changeDirectoryExt(structureList, selectedElement);
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    up(structureList, structureList.getCurrentDirectory());
                }
            }
        });
        return structureList;
    }

    private static void changeDirectoryExt(StructureList structureList, DirectoryElement selectedElement) {
        if (selectedElement.canBeResolvedToDirectory()) {
            changeDirectory(structureList, selectedElement);
        }
    }

    private static void changeDirectory(StructureList structureList, DirectoryElement selectedElement) {
        try {
            structureList.setEnabled(false);
            selectedElement.resolve(new DoneCallback<ResolvedDirectory<?>>() {
                @Override
                public void done(Future<ResolvedDirectory<?>> future) {
                    try {
                        structureList.setCurrentDirectory(future.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        structureList.setEnabled(true);
                    }
                }
            });
        } catch (IOException | RuntimeException e) {
            // todo
            e.printStackTrace();
            structureList.setEnabled(true);
        }
    }

    private static void up(StructureList structureList, ResolvedDirectory<?> selectedElement) {
        if (!selectedElement.hasParent()) {
            return;
        }
        try {
            structureList.setEnabled(false);
            selectedElement.resolveParent(new DoneCallback<ResolvedDirectory<?>>() {
                @Override
                public void done(Future<ResolvedDirectory<?>> future) {
                    try {
                        structureList.setCurrentDirectory(future.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        structureList.setEnabled(true);
                    }
                }
            });
        } catch (IOException | RuntimeException e) {
            // todo
            e.printStackTrace();
            structureList.setEnabled(true);
        }
    }
}
