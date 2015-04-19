package jet.task.previewer.api.ftp;

import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.ftp.FTPClientSession;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FTPDirectoryElement}.
 */
public class FTPDirectoryElementTest {
    private FTPClientSession ftpClientSession;
    private FTPFile ftpFile;
    private FTPDirectoryElement ftpDirectoryElement;

    @Before
    public void setUp() throws Exception {
        ftpClientSession = mock(FTPClientSession.class);
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

    @Ignore
    @Test
    public void testResolve() throws Exception {
        ftpFile.setName("test-folder");
        Future<List<FTPFile>> futureMock = mock(Future.class);
        Mockito.when(futureMock.get()).thenReturn(Arrays.asList(new FTPFile()));
        when(ftpClientSession.changeWorkingDirectory("/test-folder")).thenReturn(futureMock);
        Future<ResolvedDirectory<?>> resolve = ftpDirectoryElement.resolve(future -> Assert.assertSame(futureMock, future));
        ResolvedDirectory<?> resolvedDirectory = resolve.get();
    }

    @Test
    public void testConsumeInputStream() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {
        ftpFile.setName("image.png");
        Assert.assertEquals("image.png", ftpDirectoryElement.getName());
    }
}