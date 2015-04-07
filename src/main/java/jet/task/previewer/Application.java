package jet.task.previewer;

import jet.task.previewer.ui.ApplicationWindow;
import jet.task.previewer.ui.LookAndFeelUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akoshevoy on 07.04.2015.
 */
public class Application {
    private final List<ApplicationListener> listeners = new ArrayList<>();

    public static void main(String[] args) {
        LookAndFeelUtils.enableNimbusLookAndFeel();

        Application application = new Application();
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

    public void addApplicationListener(ApplicationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeApplicationListener(ApplicationListener listener) {
        listeners.remove(listener);
    }

    private void fireApplicationClosed() {
        for (ApplicationListener listener : listeners) {
            listener.applicationClosed();
        }
    }
}
