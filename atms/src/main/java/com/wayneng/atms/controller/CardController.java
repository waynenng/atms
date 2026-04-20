package com.wayneng.atms.controller;

import com.wayneng.atms.model.Card;
import com.wayneng.atms.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/{cardNumber}")
    public Card getCardByNumber(@PathVariable String cardNumber) {
        return cardService.getCardByNumber(cardNumber);
    }

    @PostMapping("/{cardNumber}/validate-pin")
    public boolean validatePin(
            @PathVariable String cardNumber,
            @RequestParam String pin
    ) {
        return cardService.validatePin(cardNumber, pin);
    }

    @PostMapping("/{cardNumber}/block")
    public void blockCard(@PathVariable String cardNumber) {
        cardService.blockCard(cardNumber);
    }

    @GetMapping("/account/{accountNumber}")
    public List<Card> getCardsByAccount(@PathVariable String accountNumber) {
        return cardService.getCardsByAccount(accountNumber);
    }
}