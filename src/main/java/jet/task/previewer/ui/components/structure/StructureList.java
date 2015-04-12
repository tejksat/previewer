package jet.task.previewer.ui.components.structure;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.fs.FileDirectoryResolver;
import jet.task.previewer.ui.EventUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JList;
import javax.swing.ListModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class StructureList extends JList<DirectoryElement> {
    private final Logger logger = LoggerFactory.getLogger(StructureList.class);

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

    public void updateCurrentDirectory(@NotNull ResolvedDirectory<?> currentDirectory) {
        StructureListModel model = getThisModel();
        model.setCurrentDirectory(currentDirectory);
        requestFocus();
        if (!model.isEmpty()) {
            setSelectedIndex(0);
        }
    }

    public void disposeResources() {
        ResolvedDirectory<?> currentDirectory = getCurrentDirectory();
        if (currentDirectory != null) {
            currentDirectory.dispose();
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
                        structureList.changeDirectoryExt(element);
                    }
                }
            }
        });
        structureList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DirectoryElement selectedElement = structureList.getSelectedValue();
                    structureList.changeDirectoryExt(selectedElement);
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    structureList.moveUp();
                }
            }
        });
        return structureList;
    }

    private void changeDirectoryExt(DirectoryElement selectedElement) {
        if (selectedElement.canBeResolvedToDirectory()) {
            changeDirectory(selectedElement);
        }
    }

    private void changeDirectory(@NotNull DirectoryElement selectedElement) {
        setEnabled(false);
        try {
            selectedElement.resolve(future -> {
                try {
                    updateCurrentDirectory(future.get());
                } catch (InterruptedException e) {
                    logger.debug("Changing directory to [{}] has been interrupted", selectedElement.getName(), e);
                } catch (ExecutionException e) {
                    logger.error("Changing directory to [{}] failed with exception", selectedElement.getName(), e);
                } finally {
                    setEnabled(true);
                }
            });
        } catch (IOException | RuntimeException e) {
            logger.error("Failed to change directory to [{}]", selectedElement.getName(), e);
            setEnabled(true);
        }
    }

    public void moveUp() {
        moveUp(getCurrentDirectory());
    }

    private void moveUp(@NotNull ResolvedDirectory<?> selectedElement) {
        if (!selectedElement.hasParent()) {
            return;
        }
        setEnabled(false);
        try {
            selectedElement.resolveParent(future -> {
                try {
                    updateCurrentDirectory(future.get());
                } catch (InterruptedException e) {
                    logger.debug("Changing current directory to parent directory has been interrupted", e);
                } catch (ExecutionException e) {
                    logger.error("Changing current directory to parent directory failed with exception", e);
                } finally {
                    setEnabled(true);
                }
            });
        } catch (IOException | RuntimeException e) {
            logger.error("Changing current directory to parent directory failed with exception", e);
            setEnabled(true);
        }
    }

    public void setCurrentFileSystemPath(@NotNull Path path) {
        setEnabled(false);
        FileDirectoryResolver.submit(path, future -> {
            try {
                updateCurrentDirectory(future.get());
            } catch (InterruptedException e) {
                logger.debug("Changing current directory to [{}] has been interrupted", path, e);
            } catch (ExecutionException e) {
                logger.error("Changing current directory to [{}] failed with exception", path, e);
            } finally {
                setEnabled(true);
            }
        });
    }
}
