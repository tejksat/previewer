package jet.task.previewer.ui.preview;

import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * Created by Alex Koshevoy on 28.03.2015.
 */
public class PreviewComponent extends JComponent {
    public static final String WELCOME_TEXT = "Select a file";
    public static final String IMAGE_LOAD_FAILED_TEXT = "Image load failed";
    public static final String PREVIEW_CANCELLED_TEXT = "Cancelled";

    private final JLabel informationLabel;
    private Image image;

    public PreviewComponent() {
        super();

        setLayout(new BorderLayout());
        this.informationLabel = new JLabel(WELCOME_TEXT, SwingConstants.CENTER);
        add(informationLabel, BorderLayout.CENTER);
    }

    public void updateImage(Image image) {
        if (image == null) {
            nothingToPreview();
        } else {
            setImage(image);
        }
    }

    public void setImage(@NotNull Image image) {
        this.informationLabel.setVisible(false);
        this.image = image;

        repaint();
        System.out.println("repaint() called");
    }

    public void userCancelledPreview() {
        informationLabel.setText(PREVIEW_CANCELLED_TEXT);
        processImageRemoval();
    }

    public void nothingToPreview() {
        informationLabel.setText(WELCOME_TEXT);
        processImageRemoval();
    }

    public void imageLoadFailed() {
        informationLabel.setText(IMAGE_LOAD_FAILED_TEXT);
        processImageRemoval();
    }

    private void processImageRemoval() {
        image = null;
        informationLabel.setVisible(true);
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
        System.out.println("Repaint preview with clip bounds: " + g.getClipBounds());
        System.out.println("Preview bounds: " + getBounds());
/*
        Rectangle bounds = getBounds();
        g = g.create(bounds.x, bounds.y, bounds.width, bounds.height);
*/
        if (image != null) {
            Rectangle componentBounds = getBounds();
            if (componentBounds.intersects(g.getClipBounds())) {
                // todo render component within clip bounds
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);
                double scaleFactor = Math.min((double) componentBounds.width / imageWidth, (double) componentBounds.height / imageHeight);
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
