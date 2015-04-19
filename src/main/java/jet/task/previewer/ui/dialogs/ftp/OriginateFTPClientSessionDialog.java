package jet.task.previewer.ui.dialogs.ftp;

import jet.task.previewer.common.StringUtils;
import jet.task.previewer.ftp.FTPClientSession;
import jet.task.previewer.ui.StatusHolder;
import jet.task.previewer.ui.main.ApplicationWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;

public class OriginateFTPClientSessionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonConnect;
    private JButton buttonCancel;
    private JTextField addressTextField;
    private JTextField userTextField;
    private JPasswordField passwordField;

    private StatusHolder statusHolder;

    private FTPClientSession ftpClientSession;

    public OriginateFTPClientSessionDialog() {
        this(null);
    }

    public OriginateFTPClientSessionDialog(Frame owner) {
        super(owner, "New FTP Session");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonConnect);

        buttonConnect.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        connect();
    }

    private void connect() {
        String address = addressTextField.getText();
        if (StringUtils.isEmpty(address)) {
            JOptionPane.showMessageDialog(this, "Please, specify FTP server address.",
                    "Address is empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int delimiter = address.lastIndexOf(":");
        FTPClientSessionOriginator worker;
        if (delimiter == -1) {
            worker = new FTPClientSessionOriginator(address, callback);
        } else {
            String host = address.substring(0, delimiter);
            int port;
            try {
                port = Integer.valueOf(address.substring(delimiter + 1));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please, specify either host or host and port delimited by colon.",
                        "Wrong address specified", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            worker = new FTPClientSessionOriginator(host, port, callback);
        }
        String username = userTextField.getText();
        if (StringUtils.isNotEmpty(username)) {
            worker.setCredentials(username, new String(passwordField.getPassword()));
        }
        worker.execute();
        activateComponents(false);
        statusHolder.updateStatus(String.format("Connecting to %s...", address));
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        OriginateFTPClientSessionDialog dialog = new OriginateFTPClientSessionDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
    }

    private Collection<JComponent> getActiveComponents() {
        return Arrays.asList(addressTextField, userTextField, passwordField, buttonConnect, buttonCancel);
    }

    public void setStatusHolder(@NotNull StatusHolder statusHolder) {
        this.statusHolder = statusHolder;
    }

    public FTPClientSession getFTPClientSession() {
        return ftpClientSession;
    }

    private final FTPClientSessionOriginator.FTPConnectionCallback callback = new FTPClientSessionOriginator.FTPConnectionCallback() {
        @Override
        public void connectionEstablished(@NotNull FTPClientSession ftpClientSession) {
            OriginateFTPClientSessionDialog.this.ftpClientSession = ftpClientSession;
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
        OriginateFTPClientSessionDialog dialog = new OriginateFTPClientSessionDialog(applicationWindow);
        dialog.setStatusHolder(applicationWindow);
        dialog.pack();
        dialog.setLocationRelativeTo(applicationWindow);
        dialog.setVisible(true);
        return dialog.getFTPClientSession();
    }
}
