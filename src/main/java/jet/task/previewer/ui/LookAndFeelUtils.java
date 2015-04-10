package jet.task.previewer.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Contain utility methods to apply fancy Swing Look and Feel.
 */
public class LookAndFeelUtils {
    public static final String NIMBUS_LAF_NAME = "Nimbus";

    private static final Logger logger = LoggerFactory.getLogger(LookAndFeelUtils.class);

    private LookAndFeelUtils() {
    }

    public static void enableNimbusLookAndFeel() {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if (NIMBUS_LAF_NAME.equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("Unable to set {} look and feel for Swing", NIMBUS_LAF_NAME, e);
            }
        });
    }
}
