package jet.task.previewer.ui;

import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.ftp.FTPResolver;
import jet.task.previewer.ui.ftp.dialog.NewFTPSessionDialog;
import jet.task.previewer.ui.preview.PreviewComponent;
import jet.task.previewer.ui.preview.StructureListSelectionListener;
import jet.task.previewer.ui.structure.RootsResolvedDirectory;
import jet.task.previewer.ui.structure.StructureList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class Application extends JFrame implements StatusHolder {

    public static final String FTP_ROOT_PATHNAME = "/";
    private final JLabel statusBar;

    private final Logger logger = LoggerFactory.getLogger(Application.class);

    public Application() {
        // TODO get version from project properties
        super("Jet PreViewer 1.0");

        setIconImages(ImageUtils.createImages("/icons/paper-airplane-small.png", "/icons/paper-airplane-large.png"));

        // structure
        StructureList structureList = StructureList.newInstance();
        testEntryList(structureList);
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
        // todo refactor (!)
        // todo commented because of partial commit
        structureList.addListSelectionListener(new StructureListSelectionListener(structureList, previewComponent));

        // tool bar
        JToolBar toolBar = new JToolBar();
        toolBar.add(new AbstractAction("Up", ImageUtils.createImageIcon("/icons/toolbar/arrow-up.png", "Up")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo ???
            }
        });
        toolBar.addSeparator();
        toolBar.add(new AbstractAction("Home", ImageUtils.createImageIcon("/icons/toolbar/home.png", "Home")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo go to user home
            }
        });
        toolBar.add(new AbstractAction("FTP Server", ImageUtils.createImageIcon("/icons/toolbar/ftp.png", "FTP Icon")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                FTPClientSession ftpClient = NewFTPSessionDialog.requestFTPClient(Application.this);
                if (ftpClient == null) {
                    return;
                }
                FTPResolver ftpResolver = new FTPResolver(ftpClient, FTP_ROOT_PATHNAME, new DoneCallback<ResolvedDirectory<?>>() {
                    @Override
                    public void done(Future<ResolvedDirectory<?>> future) {
                        if (future.isCancelled()) {
                            ftpClient.disconnect();
                        } else {
                            try {
                                ResolvedDirectory<?> resolvedDirectory = future.get();
                                structureList.setCurrentDirectory(resolvedDirectory);
                            } catch (InterruptedException exc) {
                                logger.error("Root directory resolution has been interrupted", exc);
                                ftpClient.disconnect();
                            } catch (ExecutionException exc) {
                                logger.error("Root directory resolution failed with exception", exc);
                                ftpClient.disconnect();
                            }
                        }
                    }
                });
                ftpResolver.execute();
            }
        });
        contentPane.add(toolBar, BorderLayout.NORTH);

        // status bar
        statusBar = new JLabel(" ");
        statusBar.setBorder(new EtchedBorder());
        contentPane.add(statusBar, BorderLayout.SOUTH);

        setContentPane(contentPane);
    }

    private void testEntryList(StructureList structureList) {
        structureList.setCurrentDirectory(new RootsResolvedDirectory());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Application application = new Application();
            application.setPreferredSize(new Dimension(500, 500));
            application.pack();
            application.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            application.setVisible(true);
        });
    }

    @Override
    public void updateStatus(String status) {
        statusBar.setText(status);
    }
}
