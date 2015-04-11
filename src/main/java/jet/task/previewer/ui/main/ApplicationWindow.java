package jet.task.previewer.ui.main;

import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.api.fs.RootsResolvedDirectory;
import jet.task.previewer.api.ftp.FTPResolver;
import jet.task.previewer.common.FileSystemUtils;
import jet.task.previewer.common.StringUtils;
import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ftp.FTPClientUtils;
import jet.task.previewer.ui.DispatchThreadUtils;
import jet.task.previewer.ui.ImageUtils;
import jet.task.previewer.ui.StatusHolder;
import jet.task.previewer.ui.components.preview.PreviewComponent;
import jet.task.previewer.ui.components.preview.StructureListSelectionListener;
import jet.task.previewer.ui.components.structure.StructureList;
import jet.task.previewer.ui.dialogs.ftp.NewFTPSessionDialog;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

/**
 * Main application window.
 */
public class ApplicationWindow extends JFrame implements StatusHolder {
    private static final String WINDOW_TITLE = "Jet Viewer 0.5 beta";

    /*
     * Icon resources
     */
    private static final String APPLICATION_ICON_SMALL = "/icons/paper-airplane-small.png";
    private static final String APPLICATION_ICON_LARGE = "/icons/paper-airplane-large.png";
    private static final String ARROW_UP_ICON = "/icons/toolbar/arrow-up.png";
    private static final String HOME_ICON = "/icons/toolbar/home.png";
    private static final String FTP_ICON = "/icons/toolbar/ftp.png";

    /*
     * Action labels
     */
    private static final String HOME_ACTION_LABEL = "Home";
    private static final String UP_ACTION_LABEL = "Up";
    private static final String NEW_FTP_CONNECTION_ACTION_LABEL = "FTP Server";

    private static final String EMPTY_STATUS_TEXT = " ";

    private final JLabel statusBar;

    private final Logger logger = LoggerFactory.getLogger(ApplicationWindow.class);

    public ApplicationWindow() {
        super(WINDOW_TITLE);

        setIconImages(ImageUtils.createImages(APPLICATION_ICON_SMALL, APPLICATION_ICON_LARGE));

        // structure
        StructureList structureList = StructureList.newInstance();
        startEntryList(structureList);
        JScrollPane structurePane = new JScrollPane(structureList);

        // preview
        JPanel previewPanel = new JPanel(new BorderLayout());
        PreviewComponent previewComponent = new PreviewComponent();
        previewPanel.add(previewComponent, BorderLayout.CENTER);

        // structure + preview
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, structurePane, previewPanel);
        splitPane.setResizeWeight(0.5);
        Container contentPane = new JPanel(new BorderLayout());
        contentPane.add(splitPane, BorderLayout.CENTER);

        structureList.addListSelectionListener(new StructureListSelectionListener(structureList, previewComponent));

        // tool bar
        JToolBar toolBar = new JToolBar();
        toolBar.add(new AbstractAction(UP_ACTION_LABEL, ImageUtils.createImageIcon(ARROW_UP_ICON, "Up")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                structureList.moveUp();
            }
        });
        toolBar.addSeparator();
        toolBar.add(new AbstractAction(HOME_ACTION_LABEL, ImageUtils.createImageIcon(HOME_ICON, "Home")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path userHomePath = FileSystemUtils.getUserHomePath();
                if (userHomePath == null) {
                    JOptionPane.showMessageDialog(ApplicationWindow.this, "We don't know were you home folder is :(", "Oops", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    structureList.setCurrentFileSystemPath(userHomePath);
                }
            }
        });
        toolBar.add(new AbstractAction(NEW_FTP_CONNECTION_ACTION_LABEL, ImageUtils.createImageIcon(FTP_ICON, "FTP Icon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewFTPSessionDialog(structureList);
            }
        });
        contentPane.add(toolBar, BorderLayout.NORTH);

        // status bar
        statusBar = new JLabel(EMPTY_STATUS_TEXT);
        statusBar.setBorder(new EtchedBorder());
        contentPane.add(statusBar, BorderLayout.SOUTH);

        // actions on close
        addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // clean resources
                structureList.disposeResources();
            }
        });

        setContentPane(contentPane);
    }

    private void showNewFTPSessionDialog(StructureList structureList) {
        FTPClientSession ftpClient = NewFTPSessionDialog.requestFTPClient(ApplicationWindow.this);
        if (ftpClient == null) {
            return;
        }
        FTPResolver.submit(ftpClient, FTPClientUtils.FTP_ROOT_PATHNAME, future -> {
            if (future.isCancelled()) {
                ftpClient.close();
            } else {
                try {
                    ResolvedDirectory<?> resolvedDirectory = future.get();
                    structureList.updateCurrentDirectory(resolvedDirectory);
                } catch (InterruptedException exc) {
                    logger.error("FTP root directory listing has been interrupted", exc);
                    ftpClient.close();
                } catch (ExecutionException exc) {
                    logger.error("FTP root directory listing failed with exception", exc);
                    ftpClient.close();
                }
            }
        });
    }

    private void startEntryList(StructureList structureList) {
        structureList.updateCurrentDirectory(RootsResolvedDirectory.newInstance());
    }

    @Override
    public void updateStatus(@Nullable String status) {
        DispatchThreadUtils.invokeASAP(() -> statusBar.setText(StringUtils.defaultIfEmpty(status, EMPTY_STATUS_TEXT)));
    }
}
