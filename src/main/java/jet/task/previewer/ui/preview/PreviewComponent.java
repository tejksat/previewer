package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.ImageUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class PreviewComponent extends JComponent {
    public static final String WELCOME_TEXT = "Select a file";
    public static final String IMAGE_LOAD_FAILED_TEXT = "Image load failed";
    public static final String PREVIEW_CANCELLED_TEXT = "Cancelled";

    private final JLabel informationLabel;
    private final JTextArea textArea;
    private Image image;

    private final ImageIcon loaderImageIcon = ImageUtils.createImageIcon("/icons/loader.gif", "Loader icon");

    public PreviewComponent() {
        super();

        setLayout(new BorderLayout());

        informationLabel = new JLabel(WELCOME_TEXT, SwingConstants.CENTER);

        textArea = new JTextArea();
        textArea.setVisible(false);
        textArea.setEditable(false);

        // todo (check if JDK bug) uncommenting next line causes application running after closing the dialog
//        textArea.getCaret().setVisible(true);

        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);

        add(informationLabel, BorderLayout.CENTER);
        add(textArea, BorderLayout.CENTER);
    }

    public void updateImage(Image image) {
        if (image == null) {
            nothingToPreview();
        } else {
            setImage(image);
        }
    }

    public void setImage(@NotNull Image newImage) {
        removeAll();

        informationLabel.setVisible(false);
        textArea.setVisible(false);
        textArea.setText(null);
        image = newImage;

        repaint();
    }

    public void setTextPreview(String text) {
        informationLabel.setVisible(false);
        textArea.setVisible(true);
        textArea.setText(text);
        image = null;

        repaint();
    }

    public void loadingPreview() {
        informationLabel.setIcon(loaderImageIcon);
        processPreviewRemoval();
    }

    public void userCancelledPreview() {
        informationLabel.setText(PREVIEW_CANCELLED_TEXT);
        processPreviewRemoval();
    }

    public void nothingToPreview() {
        informationLabel.setText(WELCOME_TEXT);
        processPreviewRemoval();
    }

    public void imageLoadFailed() {
        informationLabel.setText(IMAGE_LOAD_FAILED_TEXT);
        processPreviewRemoval();
    }

    private void processPreviewRemoval() {
        image = null;
        textArea.setVisible(false);
        textArea.setText(null);

        informationLabel.setVisible(true);

        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            return super.getPreferredSize();
        } else {
            return new Dimension(image.getWidth(this), image.getHeight(this));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Rectangle componentBounds = getBounds();
            if (componentBounds.intersects(g.getClipBounds())) {
                // todo render component within clip bounds
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);
                double scaleFactor = Math.min(1.0, Math.min((double) componentBounds.width / imageWidth, (double) componentBounds.height / imageHeight));
                int paintWidth = (int) (imageWidth * scaleFactor);
                int paintHeight = (int) (imageHeight * scaleFactor);
                Rectangle paintRectangle = new Rectangle(componentBounds.x + (componentBounds.width - paintWidth) / 2,
                        componentBounds.y + (componentBounds.height - paintHeight) / 2,
                        paintWidth,
                        paintHeight);
                g.drawImage(
                        image,
                        paintRectangle.x,
                        paintRectangle.y,
                        paintRectangle.width,
                        paintRectangle.height,
                        this
                );
            }
        }
    }
}
