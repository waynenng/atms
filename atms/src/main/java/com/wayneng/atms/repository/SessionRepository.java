package com.wayneng.atms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wayneng.atms.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByAtmAtmCodeAndSessionStatus(String atmCode, String sessionStatus);

    Optional<Session> findBySessionId(String sessionId);
}