package com.gigajet.mhlb.domain.user.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    int countByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE users SET image = ?1 WHERE id = ?2 ")
    void updateImage(String image, Long id);

    @Modifying
    @Transactional
    @Query("UPDATE users SET username = ?1 WHERE id = ?2 ")
    void updateUserName(String username, Long id);

    @Modifying
    @Transactional
    @Query("UPDATE users SET description = ?1 WHERE id = ?2 ")
    void updateDescription(String description, Long id);

    @Modifying
    @Transactional
    @Query("UPDATE users SET job = ?1 WHERE id = ?2 ")
    void updateJob(String job, Long id);
}
