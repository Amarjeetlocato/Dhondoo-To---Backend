package com.whoami.launch.config;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.whoami.launch.exception.MediaValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaValidationService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;   // 5 MB
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;  // 50 MB

    private final Cloudinary cloudinary;

    public void validateThumbnail(String publicId) throws Exception {

        Map<?, ?> resource = cloudinary.api().resource(
                publicId,
                ObjectUtils.asMap(
                        "resource_type",
                        "image"
                ));

        Object bytesObj = resource.get("bytes");

        if (bytesObj == null) {
            throw new MediaValidationException(
                    "Unable to validate image. Please upload again."
            );
        }

        long bytes = ((Number) bytesObj).longValue();

        if (bytes > MAX_IMAGE_SIZE) {
            throw new MediaValidationException(
                    "Image is too large. Maximum size allowed is 5 MB."
            );
        }
    }

    public void validateVideo(String publicId) throws Exception {

        Map<?, ?> resource = cloudinary.api().resource(
                publicId,
                ObjectUtils.asMap(
                        "resource_type",
                        "video"
                ));

        Object bytesObj = resource.get("bytes");

        if (bytesObj == null) {
            throw new MediaValidationException(
                    "Unable to validate video. Please upload again."
            );
        }

        long bytes = ((Number) bytesObj).longValue();

        if (bytes > MAX_VIDEO_SIZE) {
            throw new MediaValidationException(
                    "Video is too large. Maximum size allowed is 50 MB."
            );
        }
    }
}