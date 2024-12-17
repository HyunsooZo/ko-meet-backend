package com.backend.immilog.image.application.service.query;

import com.backend.immilog.image.domain.model.Image;
import com.backend.immilog.image.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageQueryService {
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public Image getImageByPath(String imagePath) {
        return imageRepository.findByPath(imagePath);
    }
}
