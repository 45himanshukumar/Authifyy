package com.himanshu.Authifyy.repositry;

import com.himanshu.Authifyy.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepositry extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean  existsByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);
}
