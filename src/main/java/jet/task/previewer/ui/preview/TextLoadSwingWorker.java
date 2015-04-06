package jet.task.previewer.ui.preview;

import jet.task.previewer.ui.engine.DirectoryElement;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex Koshevoy on 04.04.2015.
 */
public class TextLoadSwingWorker extends PreviewLoadSwingWorker<String> {
    public static final int BUFFER_SIZE = 1024;

    public TextLoadSwingWorker(@NotNull DirectoryElement<?> element, @NotNull PreviewComponent previewComponent) {
        super(element, previewComponent);
    }

    @NotNull
    @Override
    protected String consumeInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            // todo preserve char buffer
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
        // todo text load failed (!)
        previewComponent.imageLoadFailed();
    }

    @Override
    protected void executionSucceeded(String result) {
        previewComponent.setTextPreview(result);
    }
}
