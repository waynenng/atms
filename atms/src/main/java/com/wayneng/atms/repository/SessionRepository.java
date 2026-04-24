package com.wayneng.atms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wayneng.atms.model.Session;

public interface SessionRepository extends JpaRepository<Session, String> {

    Optional<Session> findByAtm_AtmCodeAndSessionStatus(String atmCode, String sessionStatus);

    Optional<Session> findBySessionId(String sessionId);
}