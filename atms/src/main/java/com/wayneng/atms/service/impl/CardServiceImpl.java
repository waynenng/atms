package com.wayneng.atms.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.wayneng.atms.model.Card;
import com.wayneng.atms.repository.CardRepository;
import com.wayneng.atms.service.CardService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    // APPROVED
    private final CardRepository cardRepository;

    // APPROVED
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public Card getCardByNumber(String cardNumber) {
        return cardRepository
            .findByCardNumber(cardNumber)
            .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    // APPROVED
    @Override
    public Card getActiveCard(String cardNumber) {
        return cardRepository
            .findByCardNumberAndCardStatus(cardNumber, "ACTIVE")
            .orElseThrow(() -> new RuntimeException("Card is not active"));
    }

    // APPROVED
    @Override
    public boolean validatePin(String cardNumber, String rawPin) {
        Card card = getActiveCard(cardNumber);

        boolean isValid = card.getPinHash().equals(rawPin);

        if (isValid) {
            resetFailedAttempts(cardNumber);
        } else {
            incrementFailedAttempts(cardNumber);
        }

        return isValid;
    }

    // APPROVED
    @Override
    public void incrementFailedAttempts(String cardNumber) {
        Card card = getCardByNumber(cardNumber);

        int attempts = card.getFailedPinAttempts() + 1;
        card.setFailedPinAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            card.setCardStatus("BLOCKED");
        }

        cardRepository.save(card);
    }

    // APPROVED
    @Override
    public void resetFailedAttempts(String cardNumber) {
        Card card = getCardByNumber(cardNumber);

        card.setFailedPinAttempts(0);
        cardRepository.save(card);
    }

    // APPROVED
    @Override
    public void blockCard(String cardNumber) {
        Card card = getCardByNumber(cardNumber);

        card.setCardStatus("BLOCKED");
        cardRepository.save(card);
    }

    // APPROVED
    @Override
    public List<Card> getCardsByAccount(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }
}
