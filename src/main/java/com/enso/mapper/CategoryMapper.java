package com.enso.mapper;

import com.enso.dto.response.CategoryResponse;
import com.enso.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
}
