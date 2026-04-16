package com.wayneng.atms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wayneng.atms.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByCardCardNumberAndSessionStatus(String cardNumber, String sessionStatus);

    Optional<Session> findByCardCardNumber(String cardNumber);

    List<Session> findBySessionStatus(String sessionStatus);

    List<Session> findByAuthenticated(Boolean authenticated);

    List<Session> findByStartTimeAfter(LocalDateTime time);
}