package jet.task.previewer.ui.components.preview;

import jet.task.previewer.api.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Loads text for preview.
 */
public class TextLoadSwingWorker extends PreviewLoadSwingWorker<String> {
    public static final int BUFFER_SIZE = 2048;

    public TextLoadSwingWorker(@NotNull DirectoryElement element, @NotNull PreviewComponent previewComponent) {
        super(element, previewComponent);
    }

    @NotNull
    @Override
    protected String consumeInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CharBuffer charBuffer = CharBuffer.allocate(BUFFER_SIZE);
            reader.read(charBuffer);
            charBuffer.flip();
            boolean reachedTheEnd = reader.read() == -1;
            String text = charBuffer.toString();
            return reachedTheEnd ? text : text + "...";
        }
    }

    @Override
    protected void executionFailed(ExecutionException e) {
        logger.warn("Text load failed", e);
        previewComponent.textLoadFailed();
    }

    @Override
    protected void executionSucceeded(String result) {
        previewComponent.setTextPreview(result);
    }
}
