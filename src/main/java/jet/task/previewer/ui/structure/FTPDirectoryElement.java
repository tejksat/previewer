package jet.task.previewer.ui.structure;

import jet.task.previewer.ui.engine.DoneCallback;
import jet.task.previewer.ui.engine.ResolvedDirectory;
import jet.task.previewer.ui.engine.DirectoryElement;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * Created by akoshevoy on 03.04.2015.
 */
public class FTPDirectoryElement implements DirectoryElement<FTPFile> {
    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean canBeResolvedToDirectory() {
        return false;
    }

    @Override
    public Future<ResolvedDirectory<?>> resolve(@NotNull DoneCallback<ResolvedDirectory<?>> doneCallback) throws IOException {
        return null;
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
