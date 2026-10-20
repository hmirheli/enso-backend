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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final SlugUtil slugUtil;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByDeletedFalse().stream()
                .map(productMapper::toProductResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toProductResponse(findActiveProductById(id));
    }

    @Transactional(readOnly = true)
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
            replaceImages(productEntity, request.getImages());
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

        List<String> removedImageUrls = new ArrayList<>();
        if (request.getImages() != null) {
            Set<String> requestedUrls = request.getImages().stream()
                    .filter(url -> url != null && !url.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toSet());

            removedImageUrls = productEntity.getImages().stream()
                    .map(ProductImageEntity::getImageUrl)
                    .filter(url -> !requestedUrls.contains(url))
                    .distinct()
                    .toList();

            replaceImages(productEntity, request.getImages());
        }

        productEntity.setName(request.getName());
        productEntity.setSlug(slug);
        productEntity.setDescription(request.getDescription());
        productEntity.setPrice(request.getPrice());
        productEntity.setStock(request.getStock());
        productEntity.setCategory(category);

        ProductEntity savedProduct = productRepository.save(productEntity);

        deleteImagesFromMinioAfterCommit(removedImageUrls);

        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse addProductImage(Long productId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image file is empty"
            );
        }

        String contentType = file.getContentType();
        if (contentType == null
                || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only image files are allowed"
            );
        }
        ProductEntity product = findActiveProductById(productId);
        List<ProductImageEntity> currentImages = product.getImages();

        for (int i = 0; i < currentImages.size(); i++) {
            ProductImageEntity currentImage = currentImages.get(i);
            currentImage.setPrimaryImage(i == 0);
            currentImage.setDisplayOrder(i);
        }

        String imageUrl = imageStorageService.upload(file);
        int displayOrder = currentImages.size();

        ProductImageEntity image = ProductImageEntity.builder()
                .imageUrl(imageUrl)
                .primaryImage(displayOrder == 0)
                .displayOrder(displayOrder)
                .build();

        product.addImage(image);

        return productMapper.toProductResponse(productRepository.save(product));
    }

    private void replaceImages(ProductEntity product, List<String> imageUrls) {
        product.getImages().clear();

        if (imageUrls == null) {
            return;
        }

        int displayOrder = 0;

        for (String imageUrl : imageUrls) {
            if (imageUrl == null || imageUrl.isBlank()) {
                continue;
            }

            ProductImageEntity image = ProductImageEntity.builder()
                    .imageUrl(imageUrl.trim())
                    .primaryImage(displayOrder == 0)
                    .displayOrder(displayOrder)
                    .build();

            product.addImage(image);
            displayOrder++;
        }
    }

    private void deleteImagesFromMinioAfterCommit(List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (String imageUrl : imageUrls) {
                            try {
                                imageStorageService.delete(imageUrl);
                            } catch (RuntimeException e) {
                                log.warn("Database reference was removed, but MinIO image deletion failed: {}",
                                        imageUrl,
                                        e
                                );
                            }
                        }
                    }
                }
        );
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
