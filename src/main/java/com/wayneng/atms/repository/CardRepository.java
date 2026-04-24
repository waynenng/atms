package com.wayneng.atms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wayneng.atms.model.Card;

public interface CardRepository extends JpaRepository<Card, String> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByCardNumberAndCardStatus(String cardNumber, String cardStatus);

    List<Card> findByAccount_AccountNumber(String accountNumber);
}