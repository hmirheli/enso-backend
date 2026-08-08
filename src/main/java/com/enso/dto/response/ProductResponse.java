package com.enso.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private List<String> images;
    private String category;
    private Long categoryId;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
