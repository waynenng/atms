package com.wayneng.atms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wayneng.atms.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    // USED
    Optional<Card> findByCardNumber(String cardNumber);

    // USED
    Optional<Card> findByCardNumberAndCardStatus(String cardNumber, String cardStatus);

    // USED
    List<Card> findByAccountId(Long accountId);
}