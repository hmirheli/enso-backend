package com.enso.service;

import com.enso.dto.request.ProductRequest;
import com.enso.dto.response.ProductResponse;
import com.enso.entity.CategoryEntity;
import com.enso.entity.ProductEntity;
import com.enso.entity.ProductImageEntity;
import com.enso.exception.DuplicateResourceException;
import com.enso.exception.ResourceNotFoundException;
import com.enso.mapper.ProductMapper;
import com.enso.repository.CategoryRepository;
import com.enso.repository.ProductRepository;
import com.enso.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final SlugUtil slugUtil;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByDeletedFalse().stream()
                .map(productMapper::toProductResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toProductResponse(findActiveProductById(id));
    }

    public ProductResponse getProductBySlug(String slug) {
        return productMapper.toProductResponse(
                productRepository.findBySlugAndDeletedFalse(slug)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Product", slug)
                        )
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByDeletedFalseAndCategory_Id(categoryId).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByDeletedFalseAndNameContainingIgnoreCase(name).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        ProductEntity productEntity = ProductEntity.builder()
                .name(request.getName())
                .slug(slugUtil.normalize(request.getSlug()))
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .enabled(true)
                .featured(false)
                .build();

        if (request.getImages() != null) {
            request.getImages().forEach(imageUrl -> {
                ProductImageEntity image = ProductImageEntity.builder()
                        .imageUrl(imageUrl).build();

                productEntity.addImage(image);
            });
        }
        return productMapper.toProductResponse(productRepository.save(productEntity));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        ProductEntity productEntity = findActiveProductById(id);

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        String slug = slugUtil.normalize(request.getSlug());
        if (productRepository.existsBySlugAndIdNot(slug, id)) {
            throw new DuplicateResourceException("Product with slug " + slug + " already exists");
        }

        productEntity.setName(request.getName());
        productEntity.setSlug(slug);
        productEntity.setDescription(request.getDescription());
        productEntity.setPrice(request.getPrice());
        productEntity.setStock(request.getStock());
        productEntity.setCategory(category);

        if (request.getImages() != null) {
            productEntity.getImages().clear();

            request.getImages().forEach(imageUrl -> {

                ProductImageEntity image = ProductImageEntity.builder()
                        .imageUrl(imageUrl)
                        .build();
                productEntity.addImage(image);
            });
        }

        return productMapper.toProductResponse(productRepository.save(productEntity));
    }

    @Transactional
    public void deleteProduct(Long id) {

        ProductEntity product = findActiveProductById(id);
        product.setDeleted(true);
    }

    public ProductEntity findActiveProductById(Long id) {

        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
