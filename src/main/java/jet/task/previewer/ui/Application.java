package jet.task.previewer.ui;

import jet.task.previewer.model.Entry;
import jet.task.previewer.ui.ftp.dialog.NewFTPSessionDialog;
import jet.task.previewer.ui.preview.PreviewComponent;
import jet.task.previewer.ui.preview.StructureListSelectionListener;
import jet.task.previewer.ui.structure.StructureList;
import org.apache.commons.net.ftp.FTPClient;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class Application extends JFrame implements StatusHolder {

    private final JLabel statusBar;

    public Application() {
        // TODO get version from project properties
        super("Jet PreViewer 1.0");

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
//        structureList.addListSelectionListener(new StructureListSelectionListener(structureList, previewComponent));

        // tool bar
        JToolBar toolBar = new JToolBar();
        toolBar.add(new AbstractAction("New FTP Session") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FTPClient ftpClient = NewFTPSessionDialog.requestFTPClient(Application.this);
                // todo do more
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
//        Path path = FileSystems.getDefault().getPath("C:\\", "Users", "Александр", "Pictures", "Picasa", "Exports");
        Path picasaExports = FileSystems.getDefault().getPath(System.getenv("userprofile"), "Pictures", "Picasa", "Exports");
        Path testDirectory = FileSystems.getDefault().getPath("C:\\", "a test");
        List<Entry> content = Arrays.asList(Entry.Factory.newFolder(picasaExports), Entry.Factory.newFolder(testDirectory));
        // todo
//        structureList.getModel().updateContent(content);
    }

    public static void main(String[] args) {
        Application application = new Application();
        application.setPreferredSize(new Dimension(500, 500));
        application.pack();
        application.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        application.setVisible(true);
    }

    @Override
    public void updateStatus(String status) {
        statusBar.setText(status);
    }
}
