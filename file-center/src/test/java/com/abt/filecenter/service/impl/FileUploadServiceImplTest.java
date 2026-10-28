package com.abt.filecenter.service.impl;

import com.abt.filecenter.config.MinioProperties;
import com.abt.filecenter.model.FileUploadRequest;
import com.abt.filecenter.model.FileUploadResponse;
import com.abt.filecenter.repository.MinioFileRepository;
import com.abt.sys.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 上传边界校验与对象键隔离测试，无需连接数据库或 MinIO。
 */
class FileUploadServiceImplTest {
    private MinioFileRepository repository;
    private MinioProperties properties;
    private FileUploadServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(MinioFileRepository.class);
        properties = new MinioProperties();
        properties.setBucket("ipms-test");
        service = new FileUploadServiceImpl(repository, properties);
    }

    @Test
    void rejectsMissingEmptyAndOversizeFilesBeforeStorage() {
        assertThrows(BusinessException.class, () -> service.save(null));
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(new MockMultipartFile("file", new byte[0]));
        assertThrows(BusinessException.class, () -> service.save(request));
        properties.setMaxSizeBytes(2);
        request.setFile(new MockMultipartFile("file", new byte[3]));
        assertThrows(BusinessException.class, () -> service.save(request));
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsPathTraversalEvenForInternalServiceCalls() {
        FileUploadRequest request = request();
        for (String directory : new String[]{"../private", "a/b", "", "a\\b"}) {
            request.setDirectory(directory);
            assertThrows(BusinessException.class, () -> service.save(request));
        }
        verifyNoInteractions(repository);
    }

    @Test
    void uploadsWithUniqueKeyAndSafeFilenameAtSizeLimit() {
        FileUploadRequest request = request();
        properties.setMaxSizeBytes(3);
        when(repository.save(eq("ipms-test"), anyString(), same(request.getFile()),
                eq("application/octet-stream"))).thenReturn("test-etag");
        FileUploadResponse first = service.save(request);
        FileUploadResponse second = service.save(request);
        assertEquals("report.PDF", first.getOriginalName());
        assertTrue(first.getObjectKey().matches("uploads/\\d{8}/[0-9a-f-]{36}\\.pdf"));
        assertNotEquals(first.getObjectKey(), second.getObjectKey());
        assertEquals("ipms-test", first.getBucket());
        assertEquals(3, first.getSize());
        assertEquals("test-etag", first.getEtag());
        assertEquals("application/octet-stream", first.getContentType());
    }

    private FileUploadRequest request() {
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(new MockMultipartFile("file", "C:\\temp\\report.PDF", null, new byte[]{1, 2, 3}));
        return request;
    }
}
