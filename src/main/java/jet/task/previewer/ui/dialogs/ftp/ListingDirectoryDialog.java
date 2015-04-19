package jet.task.previewer.ui.dialogs.ftp;

import jet.task.previewer.ui.main.ApplicationWindow;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Future;

public class ListingDirectoryDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;

    private Future<?> future;

    private final Logger logger = LoggerFactory.getLogger(ListingDirectoryDialog.class);

    public ListingDirectoryDialog(Frame owner, @NotNull String hostname) {
        super(owner, "FTP server - " + hostname);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        setMinimumSize(new Dimension(250, 100));
    }

    private void onCancel() {
        logger.debug("User request to cancel directory listing received");
        if (future != null) {
            future.cancel(true);
        }
        dispose();
    }

    public static void main(String[] args) {
        ListingDirectoryDialog dialog = new ListingDirectoryDialog(null, "ftp.server.org");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setFuture(@NotNull Future<?> future) {
        this.future = future;
    }

    public static ListingDirectoryDialog createDialog(@NotNull ApplicationWindow applicationWindow,
                                                      @NotNull String hostname) {
        ListingDirectoryDialog dialog = new ListingDirectoryDialog(applicationWindow, hostname);
        dialog.pack();
        dialog.setLocationRelativeTo(applicationWindow);
        return dialog;
    }
}
