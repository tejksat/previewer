package jet.task.previewer.ui.components.fs;

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
public class FileList extends JList<DirectoryElement> {
    private final Logger logger = LoggerFactory.getLogger(FileList.class);

    public FileList() {
        super(new FileListModel());
    }

    @Override
    public void setModel(ListModel<DirectoryElement> model) {
        throw new UnsupportedOperationException(FileList.class + " does not support arbitrary model change");
    }

    private FileListModel getThisModel() {
        return (FileListModel) getModel();
    }

    public void updateCurrentDirectory(@NotNull ResolvedDirectory<?> currentDirectory) {
        updateCurrentDirectory(currentDirectory, true);
    }

    private void updateCurrentDirectory(@NotNull ResolvedDirectory<?> currentDirectory, boolean disposeResources) {
        FileListModel model = getThisModel();
        model.setCurrentDirectory(currentDirectory, disposeResources);
        requestFocus();
        if (!model.isEmpty()) {
            setSelectedIndex(0);
            ensureIndexIsVisible(0);
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

    public static FileList newInstance() {
        FileList fileList = new FileList();
        fileList.setCellRenderer(new FileListCellRenderer());
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (EventUtils.isPrimaryActionDoubleClick(e)) {
                    Optional<Integer> listIndex = EventUtils.getListIndexAtPoint(fileList, e.getPoint());
                    if (listIndex.isPresent()) {
                        DirectoryElement element = fileList.getThisModel().getElementAt(listIndex.get());
                        fileList.browseDirectoryIfResolvable(element);
                    }
                }
            }
        });
        fileList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DirectoryElement selectedElement = fileList.getSelectedValue();
                    fileList.browseDirectoryIfResolvable(selectedElement);
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    fileList.moveUp();
                }
            }
        });
        return fileList;
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
