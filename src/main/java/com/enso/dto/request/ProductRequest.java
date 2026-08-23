package com.enso.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 150, message = "Slug must not exceed 150 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9\\s-]+$",
            message = "Slug can only contain letters, numbers, spaces and hyphens"
    )
    private String slug;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", inclusive = false)
    private BigDecimal price;

    @NotNull(message = "Category is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private List<String> images;
}
