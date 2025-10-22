package com.Booking_care.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.Booking_care.util.error.StorageException;

@Component
public class FileUpload {
    public static final long MAX_FILE_SIZE = 7L * 1024 * 1024;

    public static final Set<String> ALLOWED_EXTS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "web");

    public static void assertAllowed(MultipartFile file) throws StorageException {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new StorageException("Max file size is 2MB");
        }

        String original = sanitizeFilename(file.getOriginalFilename());
        String ext = extensionOf(original);

        // Kiểm tra đuôi file
        if (!ALLOWED_EXTS.contains(ext)) {
            throw new StorageException("Only " + ALLOWED_EXTS + "files are allowed");
        }

        // Kiểm tra nôi dung file
        // try (InputStream is = file.getInputStream()) {
        // if (ImageIO.read(is) == null) { // không decode được ảnh
        // throw new StorageException("Invalid image data.");
        // }
        // } catch (IOException e) {
        // throw new StorageException("Cannot read upload file.");
        // }

    }

    /** Loại bỏ path, ký tự nguy hiểm, chống tên bắt đầu bằng '.' */
    public static String sanitizeFilename(String name) {
        if (name == null)
            return "file";
        name = name.replace("\\", "/");
        name = name.substring(name.lastIndexOf('/') + 1);
        name = name.replaceAll("[\\r\\n]", "");
        name = name.replaceAll("[^A-Za-z0-9._-]", "-");
        if (name.startsWith("."))
            name = "file" + name;
        return name;
    }

    /** Lấy extension, ném lỗi nếu không có đuôi */
    public static String extensionOf(String name) throws StorageException {
        int dot = name.lastIndexOf('.');
        if (dot == -1 || dot == name.length() - 1) {
            throw new StorageException("Filename must have an extension.");
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
