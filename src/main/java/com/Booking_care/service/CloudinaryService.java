package com.Booking_care.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Booking_care.domain.dto.ResCloudinaryDTO;
import com.Booking_care.util.FileUpload;
import com.Booking_care.util.error.StorageException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public ResCloudinaryDTO uploadToFolder(final MultipartFile file, final String folder, final String publicHintId)
            throws StorageException {
        FileUpload.assertAllowed(file);
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "image"); // tài nguyên ảnh
            options.put("folder", folder);
            options.put("overwrite", true); // cho phép ghi đè
            options.put("unique_filename", false); // file trùng tên -> ko thêm hậu tố cho file mới
            options.put("use_filename", false); // không dùng tên gốc của file

            if (publicHintId != null && !publicHintId.isBlank()) {
                options.put("public_id", publicHintId);
            }

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            return new ResCloudinaryDTO(publicId, url);

        } catch (Exception e) {
            throw new StorageException("Failed to upload file to Cloudinary", e);
        }
    }

    public void delete(String name) throws StorageException {
        try {
            if (name == null && name.isBlank()) {
                return;
            }
            cloudinary.uploader().destroy(name, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new StorageException("Failed to delete cloudinary resource: " + name, e);
        }
    }

}
