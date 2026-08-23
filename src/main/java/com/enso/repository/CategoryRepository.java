package com.enso.repository;

import com.enso.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByIdAndDeletedFalse(Long id);

    Optional<CategoryEntity> findBySlugAndDeletedFalse(String slug);

    Optional<CategoryEntity> findByNameAndDeletedFalse(String name);

    List<CategoryEntity> findByDeletedFalse();
}
