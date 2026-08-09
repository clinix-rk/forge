package com.clinix.forge.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DiskStorageServiceTest {

    private DiskStorageService diskStorageService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        diskStorageService = new DiskStorageService();
        ReflectionTestUtils.setField(diskStorageService, "rootLocation", tempDir.resolve("uploads"));
        diskStorageService.init();
    }

    @Test
    void store_ValidPdf_StoresFileOnDisk() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "patient_report.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        String location = diskStorageService.store(file, 1L);

        assertNotNull(location);
        assertTrue(new File(location).exists());
        byte[] loaded = diskStorageService.load(location);
        assertArrayEquals("Dummy PDF content".getBytes(), loaded);
    }

    @Test
    void store_NonPdfFile_ThrowsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> diskStorageService.store(file, 1L)
        );

        assertEquals("Only PDF files are allowed.", ex.getMessage());
    }

    @Test
    void store_FileExceeding10MB_ThrowsIllegalArgumentException() {
        byte[] largeContent = new byte[10 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                largeContent
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> diskStorageService.store(file, 1L)
        );

        assertEquals("File size exceeds limit of 10 MB.", ex.getMessage());
    }
}
