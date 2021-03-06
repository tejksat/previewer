package jet.task.previewer.api.ftp;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.ftp.FTPClientSession;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FTPDirectoryElement}.
 */
public class FTPDirectoryElementTest {
    @Mock private FTPClientSession ftpClientSession;
    @Mock private Future<List<FTPFile>> changeWorkingDirectoryFuture;
    @Mock private Future<String> consumerFuture;
    private FTPFile ftpFile;
    private FTPDirectoryElement ftpDirectoryElement;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ftpFile = new FTPFile();
        ftpDirectoryElement = new FTPDirectoryElement(ftpClientSession, "/", ftpFile);
    }

    @Test
    public void testIsDirectory() throws Exception {
        ftpFile.setType(FTPFile.DIRECTORY_TYPE);
        Assert.assertTrue(ftpDirectoryElement.isDirectory());

        ftpFile.setType(FTPFile.FILE_TYPE);
        Assert.assertFalse(ftpDirectoryElement.isDirectory());
    }

    @Test
    public void testIsFile() throws Exception {
        ftpFile.setType(FTPFile.DIRECTORY_TYPE);
        Assert.assertFalse(ftpDirectoryElement.isFile());

        ftpFile.setType(FTPFile.FILE_TYPE);
        Assert.assertTrue(ftpDirectoryElement.isFile());
    }

    @Test
    public void testCanBeResolvedToDirectory() throws Exception {
        ftpFile.setType(FTPFile.DIRECTORY_TYPE);
        Assert.assertTrue(ftpDirectoryElement.canBeResolvedToDirectory());

        ftpFile.setType(FTPFile.FILE_TYPE);
        Assert.assertFalse(ftpDirectoryElement.canBeResolvedToDirectory());

        ftpFile.setType(FTPFile.FILE_TYPE);
        ftpFile.setName("archive.zip");
        Assert.assertFalse(ftpDirectoryElement.canBeResolvedToDirectory());
    }

    @Test
    public void testResolve() throws Exception {
        ftpFile.setName("folder");
        List<FTPFile> remoteContent = Arrays.asList(FTPFiles.file("one"), FTPFiles.file("two"));
        when(changeWorkingDirectoryFuture.get()).thenReturn(remoteContent);
        when(ftpClientSession.changeWorkingDirectory("/folder")).thenReturn(changeWorkingDirectoryFuture);

        Future<ResolvedDirectory<?>> resolve = ftpDirectoryElement.resolve(future -> {});
        ResolvedDirectory<?> resolvedDirectory = resolve.get();
        List<? extends DirectoryElement> resolvedContent = resolvedDirectory.getContent();
        Assert.assertArrayEquals(
                remoteContent.stream().map(FTPFile::getName).toArray(),
                resolvedContent.stream().map(DirectoryElement::getName).toArray()
        );
    }

    @Test
    public void testConsumeInputStream() throws Exception {
        when(consumerFuture.get()).thenReturn("application.log content");
        ftpFile.setName("application.log");
        when(ftpClientSession.<String>consumeInputStream(eq("application.log"), any())).thenReturn(consumerFuture);
        String consumerResult = ftpDirectoryElement.<String>consumeInputStream(inputStream -> null);
        Mockito.verify(ftpClientSession).consumeInputStream(eq("application.log"), any());
        Mockito.verifyNoMoreInteractions(ftpClientSession);
        Assert.assertEquals("application.log content", consumerResult);
    }

    @Test
    public void testGetName() throws Exception {
        ftpFile.setName("image.png");
        Assert.assertEquals("image.png", ftpDirectoryElement.getName());
    }
}