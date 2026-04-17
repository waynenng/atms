package com.wayneng.atms.service;

import java.util.List;
import com.wayneng.atms.model.Card;

public interface CardService {

    Card getCardByNumber(String cardNumber);

    Card getActiveCard(String cardNumber);

    boolean validatePin(String cardNumber, String rawPin);

    void incrementFailedAttempts(String cardNumber);

    void resetFailedAttempts(String cardNumber);

    void blockCard(String cardNumber);

    List<Card> getCardsByAccount(Long accountId);
}
