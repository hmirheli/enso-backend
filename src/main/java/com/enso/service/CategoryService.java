package com.enso.service;

import com.enso.dto.request.CategoryRequest;
import com.enso.dto.response.CategoryResponse;
import com.enso.entity.CategoryEntity;
import com.enso.exception.DuplicateResourceException;
import com.enso.exception.ResourceNotFoundException;
import com.enso.mapper.CategoryMapper;
import com.enso.repository.CategoryRepository;
import com.enso.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final SlugUtil slugUtil;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByDeletedFalse().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.toCategoryResponse(findActiveCategoryById(id));
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        CategoryEntity category = categoryRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Slug", slug));

        return categoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String slug = slugUtil.normalize(request.getSlug());

        categoryRepository.findByNameAndDeletedFalse(request.getName()).ifPresent(category -> {
            throw new DuplicateResourceException("Category", "name", request.getName());
        });

        categoryRepository.findBySlugAndDeletedFalse(slug).ifPresent(category -> {
            throw new DuplicateResourceException("Category", "slug", slug);
        });

        CategoryEntity category = CategoryEntity.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .enabled(true)
                .build();

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        CategoryEntity category = findActiveCategoryById(id);
        String slug = slugUtil.normalize(request.getSlug());

        categoryRepository.findByNameAndDeletedFalse(request.getName())
                .filter(existingCategory -> !existingCategory.getId().equals(id))
                .ifPresent(existingCategory -> {
                    throw new DuplicateResourceException("Category", "name", request.getName());
                });

        categoryRepository.findBySlugAndDeletedFalse(slug)
                .filter(existingCategory -> !existingCategory.getId().equals(id))
                .ifPresent(existingCategory -> {
                    throw new DuplicateResourceException("Category", "slug", slug);
                });

        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {

        CategoryEntity category = findActiveCategoryById(id);
        category.setDeleted(true);
    }

    public CategoryEntity findActiveCategoryById(Long id) {
        return categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
