package com.fmi.chatappservice.repo;

import com.fmi.chatappservice.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmailContaining(String email);
    Optional<User> findUserByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    User getUserByEmail(String email);
    User getUserById(Long id);
}