package jet.task.previewer;

import jet.task.previewer.ui.LookAndFeelUtils;
import jet.task.previewer.ui.main.ApplicationWindow;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Dimension;

/**
 * Main class for the task.
 */
public class PreviewerApplication {
    public static void main(String[] args) {
        LookAndFeelUtils.enableNimbusLookAndFeel();

        PreviewerApplication application = new PreviewerApplication();
        application.start();
    }

    private void start() {
        SwingUtilities.invokeLater(() -> {
            ApplicationWindow applicationWindow = new ApplicationWindow();
            applicationWindow.setPreferredSize(new Dimension(500, 500));
            applicationWindow.pack();
            applicationWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            applicationWindow.setVisible(true);
        });
    }
}
