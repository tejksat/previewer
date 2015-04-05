package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class TextLoadSwingWorker extends PreviewLoadSwingWorker<String> {
    public TextLoadSwingWorker(@NotNull DirectoryElement<?> element, @NotNull PreviewComponent previewComponent) {
        super(element, previewComponent);
    }

    @Override
    protected String consumeInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.readLine();
        }
    }

    @Override
    protected void done() {
        try {
            if (isCancelled()) {
                switch (cancelReason) {
                    case USER_CANCEL:
                        previewComponent.userCancelledPreview();
                        return;
                    case PREVIEW_SOURCE_HAS_CHANGED:
                        // todo do something or not?
                        return;
                }
            } else {
                String text = get();
                previewComponent.setTextPreview(text);
            }
        } catch (InterruptedException e) {
            // todo why this could happen?
            e.printStackTrace();
            previewComponent.nothingToPreview();
        } catch (ExecutionException e) {
            e.printStackTrace();
            previewComponent.imageLoadFailed();
        }
    }
}
