package jet.task.previewer.api.ftp;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.ftp.FTPClientSession;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FTPResolver}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FTPResolverTest {
    @Mock private FTPClientSession ftpClientSession;
    @Mock private Future<List<FTPFile>> changeWorkingDirectoryFuture;

    @Test
    public void testSubmit() throws Exception {
        when(changeWorkingDirectoryFuture.get()).thenReturn(Arrays.asList(
                FTPFiles.directory("fiction"),
                FTPFiles.directory("non-fiction"),
                FTPFiles.file("Dontsova - 100500 Novels.fb2")
        ));
        when(ftpClientSession.changeWorkingDirectory(anyString())).thenReturn(changeWorkingDirectoryFuture);

        FTPResolver submit = FTPResolver.submit(ftpClientSession, "/usr/home/books", directory -> {});
        ResolvedDirectory<?> resolvedDirectory = submit.get();

        Assert.assertEquals("/usr/home/books", resolvedDirectory.getName());
        Assert.assertArrayEquals(new String[]{
                "fiction",
                "non-fiction",
                "Dontsova - 100500 Novels.fb2"
        }, resolvedDirectory.getContent().stream().map(DirectoryElement::getName).toArray());

        verify(ftpClientSession).changeWorkingDirectory("/usr/home/books");
        verifyNoMoreInteractions(ftpClientSession);
    }
}