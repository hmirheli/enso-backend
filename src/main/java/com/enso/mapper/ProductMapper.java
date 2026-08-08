package com.enso.mapper;

import com.enso.dto.response.ProductResponse;
import com.enso.entity.ProductEntity;
import com.enso.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "images", source = "images", qualifiedByName = "mapImages")
    ProductResponse toProductResponse(ProductEntity productEntity);

    @Named("mapImages")
    default List<String> mapImages(List<ProductImageEntity> images) {
        if (images == null) return Collections.emptyList();
        return images.stream().map(ProductImageEntity::toString).collect(Collectors.toList());
    }
}
