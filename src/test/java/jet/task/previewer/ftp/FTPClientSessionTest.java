package jet.task.previewer.ftp;

import jet.task.previewer.api.ftp.FTPFiles;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FTPClientSession}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FTPClientSessionTest {
    @Mock private FTPClient ftpClient;
    @Mock private FTPListParseEngine ftpListParseEngine;
    @Mock private InputStream inputStream;

    private FTPClientSession ftpClientSession;

    private final Logger logger = LoggerFactory.getLogger(FTPClientSessionTest.class);

    @Before
    public void setUp() throws Exception {
        ftpClientSession = new FTPClientSession(ftpClient);
    }

    @Test
    public void testGetServerAddressNotConnected() throws Exception {
        Assert.assertNull(ftpClientSession.getServerAddress());
    }

    @Test
    public void testConnectWithPort() throws Exception {
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);

        ftpClientSession.connect("just.do.it", Optional.of(989));
        verify(ftpClient).connect("just.do.it", 989);

        when(ftpClient.isConnected()).thenReturn(true);
        Assert.assertEquals("just.do.it:989", ftpClientSession.getServerAddress());
    }

    @Test(expected = IllegalStateException.class)
    public void testAlreadyConnected() throws Exception {
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);

        ftpClientSession.connect("just.do.it", Optional.of(989));
        verify(ftpClient).connect("just.do.it", 989);

        when(ftpClient.isConnected()).thenReturn(true);
        ftpClientSession.connect("no.more", Optional.empty());
    }

    @Test(expected = FTPConnectionFailedException.class)
    public void testConnectionFailed() throws Exception {
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.CANNOT_OPEN_DATA_CONNECTION);

        ftpClientSession.connect("just.do.it", Optional.empty());
    }

    @Test
    public void testConnectAndLogin() throws Exception {
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);

        ftpClientSession.connect("just.do.it", Optional.empty());
        verify(ftpClient).connect("just.do.it");

        when(ftpClient.isConnected()).thenReturn(true);
        Assert.assertEquals("just.do.it", ftpClientSession.getServerAddress());

        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        ftpClientSession.login("user", "password");
        verify(ftpClient).login("user", "password");
    }

    @Test(expected = IllegalStateException.class)
    public void testLoginToNotConnected() throws Exception {
        when(ftpClient.isConnected()).thenReturn(false);
        ftpClientSession.login("user", "password");

        verify(ftpClient, never()).login(anyString(), anyString());
    }

    @Test
    public void testChangeWorkingDirectory() throws Exception {
        // connect
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);
        ftpClientSession.connect("just.do.it", Optional.empty());
        // login
        when(ftpClient.isConnected()).thenReturn(true);
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        ftpClientSession.login("user", "password");
        // cd /
        when(ftpClient.changeWorkingDirectory("/")).thenReturn(true);
        when(ftpClient.initiateListParsing()).thenReturn(ftpListParseEngine);
        when(ftpListParseEngine.hasNext()).thenReturn(true, true, false);
        when(ftpListParseEngine.getNext(anyInt())).thenReturn(
                new FTPFile[]{FTPFiles.directory("a"), FTPFiles.directory("b"), FTPFiles.directory("c.png")},
                new FTPFile[]{FTPFiles.directory("d.txt"), FTPFiles.directory("e.log")}
        ).thenThrow(new IllegalStateException());

        List<FTPFile> ftpFiles = ftpClientSession.changeWorkingDirectory("/").get();

        Assert.assertArrayEquals(new String[]{
                "a", "b", "c.png", "d.txt", "e.log"
        }, ftpFiles.stream().map(FTPFile::getName).toArray());

        InOrder inOrder = Mockito.inOrder(ftpClient, ftpListParseEngine);
        inOrder.verify(ftpClient).changeWorkingDirectory("/");
        inOrder.verify(ftpClient).initiateListParsing();
        inOrder.verify(ftpListParseEngine).hasNext();
        inOrder.verify(ftpListParseEngine).getNext(anyInt());
        inOrder.verify(ftpListParseEngine).hasNext();
        inOrder.verify(ftpListParseEngine).getNext(anyInt());
        inOrder.verify(ftpListParseEngine).hasNext();
    }

    @Test
    public void testConsumeInputStream() throws Exception {
        // connect
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);
        ftpClientSession.connect("just.do.it", Optional.empty());
        // login
        when(ftpClient.isConnected()).thenReturn(true);
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        ftpClientSession.login("user", "password");
        // consume
        when(ftpClient.retrieveFileStream("cake.gif")).thenReturn(inputStream);
        Future<String> future = ftpClientSession.consumeInputStream("cake.gif", is -> "Yummy");

        Assert.assertEquals("Yummy", future.get());
        verify(inputStream).close();
        verify(ftpClient).completePendingCommand();
    }

    @Test
    public void testMultipleRequestsToConsumeInputStream() throws Exception {
        // connect
        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);
        ftpClientSession.connect("just.do.it", Optional.empty());
        // login
        when(ftpClient.isConnected()).thenReturn(true);
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        ftpClientSession.login("user", "password");
        // consume
        when(ftpClient.retrieveFileStream("cake.gif")).thenReturn(inputStream);
        when(ftpClient.completePendingCommand()).thenReturn(true);

        int size = 50;

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(size);

        @SuppressWarnings("unchecked") Future<String>[] futures = new Future[size];
        AtomicInteger finishedCount = new AtomicInteger(0);
        for (int i = 0; i < size; i++) {
            final int consumer = i;
            CountDownLatch consumerInitialized = new CountDownLatch(1);
            futures[i] = ftpClientSession.consumeInputStream("cake.gif", is -> {
                try {
                    consumerInitialized.countDown();
                    logger.info("[" + consumer + "] awaits for start");
                    start.await(2, TimeUnit.SECONDS);
                    finish.countDown();
                    logger.info("[" + consumer + "] finished");
                    finishedCount.incrementAndGet();
                    return "Yummy";
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            consumerInitialized.await(2, TimeUnit.SECONDS);
        }
        start.countDown();
        finish.await(2, TimeUnit.SECONDS);

        Assert.assertEquals(1, finishedCount.get());
        for (int i = 0; i < size - 1; i++) {
            Assert.assertTrue(futures[i].isCancelled());
        }
        Assert.assertEquals("Yummy", futures[size - 1].get());
    }

    @Test
    public void testClose() throws Exception {
        ftpClientSession.close();
        verify(ftpClient).disconnect();
    }
}