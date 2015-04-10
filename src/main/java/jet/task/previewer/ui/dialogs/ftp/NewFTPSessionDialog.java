package jet.task.previewer.ui.dialogs.ftp;

import jet.task.previewer.common.StringUtils;
import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ui.StatusHolder;
import jet.task.previewer.ui.main.ApplicationWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;

public class NewFTPSessionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonConnect;
    private JButton buttonCancel;
    private JTextField addressTextField;
    private JTextField userTextField;
    private JPasswordField passwordField;

    private StatusHolder statusHolder;

    private FTPClientSession ftpClient;

    public NewFTPSessionDialog() {
        this(null);
    }

    public NewFTPSessionDialog(Frame owner) {
        super(owner, "New FTP Session");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonConnect);

        buttonConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        connect();
    }

    private void connect() {
        String address = addressTextField.getText();
        int delimiter = address.lastIndexOf(":");
        EstablishFTPSessionSwingWorker worker;
        if (delimiter == -1) {
            worker = new EstablishFTPSessionSwingWorker(address, callback);
        } else {
            String host = address.substring(0, delimiter);
            // todo catch NumberFormatException
            int port = Integer.valueOf(address.substring(delimiter + 1));
            worker = new EstablishFTPSessionSwingWorker(host, port, callback);
        }
        String username = userTextField.getText();
        if (StringUtils.isNotEmpty(username)) {
            // todo more security with passwordField?
            worker.setCredentials(username, new String(passwordField.getPassword()));
        }
        worker.execute();
        activateComponents(false);
        statusHolder.updateStatus(String.format("Connecting to %s...", address));
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        NewFTPSessionDialog dialog = new NewFTPSessionDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private Collection<JComponent> getActiveComponents() {
        return Arrays.asList(addressTextField, userTextField, passwordField, buttonConnect, buttonCancel);
    }

    public void setStatusHolder(@NotNull StatusHolder statusHolder) {
        this.statusHolder = statusHolder;
    }

    public FTPClientSession getFTPClient() {
        return ftpClient;
    }

    // TODO refactor
    private final FTPConnectionCallback callback = new FTPConnectionCallback() {
        @Override
        public void connectionEstablished(@NotNull FTPClientSession ftpClient) {
            NewFTPSessionDialog.this.ftpClient = ftpClient;
            dispose();
            statusHolder.updateStatus("Connection established");
        }

        @Override
        public void connectionFailed() {
            activateComponents(true);
            statusHolder.updateStatus("Connection failed");
        }

        @Override
        public void loginFailed() {
            activateComponents(true);
            statusHolder.updateStatus("Login failed");
        }
    };

    private void activateComponents(boolean activate) {
        for (JComponent component : getActiveComponents()) {
            component.setEnabled(activate);
        }
        setEnabled(activate);
    }

    public static FTPClientSession requestFTPClient(@NotNull ApplicationWindow applicationWindow) {
        NewFTPSessionDialog dialog = new NewFTPSessionDialog(applicationWindow);
        dialog.setStatusHolder(applicationWindow);
        dialog.pack();
        dialog.setVisible(true);
        return dialog.getFTPClient();
    }
}
