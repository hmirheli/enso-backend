package com.enso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 120, message = "Category slug must not exceed 120 characters")
    private String slug;

    @Size(max = 500, message = "Category description must not exceed 500 characters")
    private String description;
}
