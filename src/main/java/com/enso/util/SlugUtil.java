package com.enso.util;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

@Component
public class SlugUtil {

    public String normalize(String slug) {

        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Slug cannot be empty.");
        }

        return slug
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
}
