package com.example.StockFlow.repository;

import com.example.StockFlow.entity.RefreshToken;
import com.example.StockFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
