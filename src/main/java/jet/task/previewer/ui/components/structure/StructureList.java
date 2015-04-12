package jet.task.previewer.ui.components.structure;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.fs.FileDirectoryResolver;
import jet.task.previewer.ui.EventDispatchThreadUtils;
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
 * List component that shows directory content.
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

    private StructureListModel getThisModel() {
        return (StructureListModel) getModel();
    }

    public void updateCurrentDirectory(@NotNull ResolvedDirectory<?> currentDirectory) {
        updateCurrentDirectory(currentDirectory, true);
    }

    private void updateCurrentDirectory(@NotNull ResolvedDirectory<?> currentDirectory, boolean disposeResources) {
        StructureListModel model = getThisModel();
        model.setCurrentDirectory(currentDirectory, disposeResources);
        requestFocus();
        if (!model.isEmpty()) {
            setSelectedIndex(0);
        }
    }

    /**
     * Disposes resources used by current resolved directory.
     */
    public void disposeCurrentDirectoryResources() {
        getThisModel().disposeCurrentDirectoryResources();
    }

    /**
     * Returns current resolved directory.
     *
     * @return current resolved directory
     */
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
                        structureList.browseDirectoryIfResolvable(element);
                    }
                }
            }
        });
        structureList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DirectoryElement selectedElement = structureList.getSelectedValue();
                    structureList.browseDirectoryIfResolvable(selectedElement);
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    structureList.moveUp();
                }
            }
        });
        return structureList;
    }

    private void browseDirectoryIfResolvable(@NotNull DirectoryElement selectedElement) {
        if (selectedElement.canBeResolvedToDirectory()) {
            browseDirectory(selectedElement);
        }
    }

    private void browseDirectory(@NotNull DirectoryElement selectedElement) {
        setEnabled(false);
        try {
            selectedElement.resolve(future -> {
                try {
                    updateCurrentDirectory(future.get(), false);
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

    /**
     * Changes current directory to its parent directory if the latter exists.
     */
    public void moveUp() {
        ResolvedDirectory<?> currentDirectory = getCurrentDirectory();
        if (currentDirectory == null || !currentDirectory.hasParent()) {
            return;
        }
        EventDispatchThreadUtils.invokeASAP(() -> setEnabled(false));
        try {
            currentDirectory.resolveParent(future -> {
                try {
                    updateCurrentDirectory(future.get(), false);
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

    /**
     * Resolves specified directory and updates current directory if resolution succeeded.
     *
     * @param path new current file system directory
     */
    public void setCurrentFileSystemPath(@NotNull Path path) {
        EventDispatchThreadUtils.invokeASAP(() -> setEnabled(false));
        FileDirectoryResolver.submit(path, future -> {
            try {
                updateCurrentDirectory(future.get(), true);
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
