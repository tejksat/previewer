package jet.task.previewer.api.ftp;

import jet.task.previewer.api.DirectoryElement;
import jet.task.previewer.api.ResolvedDirectory;
import jet.task.previewer.ftp.FTPClientSession;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FTPResolvedDirectory}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FTPResolvedDirectoryTest {
    @Mock private FTPClientSession ftpClientSession;
    @Mock private FTPDirectoryElement basket;
    @Mock private FTPDirectoryElement grandmaPicture;
    @Mock private FTPDirectoryElement jam;
    @Mock private Future<List<FTPFile>> changeWorkingDirectoryFuture;

    private FTPFile ftpDirectory;
    private FTPResolvedDirectory ftpResolvedDirectory;

    @Before
    public void setUp() throws Exception {
        ftpDirectory = new FTPFile();
        ftpDirectory.setType(FTPFile.DIRECTORY_TYPE);
        ftpDirectory.setName("On the Roof");

        ftpResolvedDirectory = new FTPResolvedDirectory(ftpClientSession, "/Karlsson/On the Roof", Arrays.asList(basket, grandmaPicture, jam));
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("/Karlsson/On the Roof", ftpResolvedDirectory.getName());
    }

    @Test
    public void testGetContent() throws Exception {
        Assert.assertEquals(Arrays.asList(basket, grandmaPicture, jam), ftpResolvedDirectory.getContent());
    }

    @Test
    public void testHasParent() throws Exception {
        Assert.assertTrue(ftpResolvedDirectory.hasParent());
    }

    @Test
    public void testResolveParent() throws Exception {
        when(changeWorkingDirectoryFuture.get()).thenReturn(Collections.singletonList(ftpDirectory));
        when(ftpClientSession.changeWorkingDirectory(anyString())).thenReturn(changeWorkingDirectoryFuture);
        Future<ResolvedDirectory<?>> future = ftpResolvedDirectory.resolveParent(f -> {});
        ResolvedDirectory<?> parent = future.get();

        Assert.assertEquals("/Karlsson", parent.getName());
        Assert.assertArrayEquals(new String[]{"On the Roof"}, parent.getContent().stream().map(DirectoryElement::getName).toArray());

        verify(ftpClientSession).changeWorkingDirectory("/Karlsson");
        verifyNoMoreInteractions(ftpClientSession);
    }

    @Test
    public void testDispose() throws Exception {
        ftpResolvedDirectory.dispose();
        verify(ftpClientSession).close();
        verifyNoMoreInteractions(ftpClientSession);
    }
}