package com.enso.repository;

import com.enso.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByMobileNumber(String mobile);

    boolean existsByMobileNumber(String mobile);
}
