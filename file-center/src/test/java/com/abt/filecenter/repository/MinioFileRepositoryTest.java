package com.abt.filecenter.repository;

import com.abt.sys.exception.BusinessException;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 对象上传参数、流关闭和存储异常转换测试。
 */
class MinioFileRepositoryTest {

    @Test
    void writesToConfiguredBucketAndClosesStream() throws Exception {
        MinioClient client = mock(MinioClient.class);
        AtomicBoolean closed = new AtomicBoolean();
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3}) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };
        MockMultipartFile file = spy(new MockMultipartFile("file", new byte[]{1, 2, 3}));
        doReturn(stream).when(file).getInputStream();
        ObjectWriteResponse response = mock(ObjectWriteResponse.class);
        when(response.etag()).thenReturn("etag");
        when(client.putObject(any(PutObjectArgs.class))).thenAnswer(invocation -> {
            PutObjectArgs args = invocation.getArgument(0);
            assertEquals("ipms-test", args.bucket());
            assertEquals("uploads/test.pdf", args.object());
            assertEquals(3, args.objectSize());
            assertEquals("application/pdf", args.contentType());
            assertArrayEquals(new byte[]{1, 2, 3}, args.stream().readAllBytes());
            return response;
        });
        assertEquals("etag", new MinioFileRepository(client)
                .save("ipms-test", "uploads/test.pdf", file, "application/pdf"));
        assertTrue(closed.get());
    }

    @Test
    void hidesStorageErrorDetailsAndClosesStreamOnFailure() throws Exception {
        MinioClient client = mock(MinioClient.class);
        ByteArrayInputStream stream = spy(new ByteArrayInputStream(new byte[]{1}));
        MockMultipartFile file = spy(new MockMultipartFile("file", new byte[]{1}));
        doReturn(stream).when(file).getInputStream();
        when(client.putObject(any(PutObjectArgs.class))).thenThrow(new IOException("internal endpoint"));
        BusinessException error = assertThrows(BusinessException.class,
                () -> new MinioFileRepository(client).save("ipms-test", "test", file, "application/pdf"));
        assertFalse(error.getMessage().contains("internal endpoint"));
        verify(stream).close();
    }
}
