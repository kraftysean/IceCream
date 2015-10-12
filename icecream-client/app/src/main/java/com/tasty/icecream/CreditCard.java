package com.tasty.icecream;

/**
 * Created by sean on 09/10/15.
 */
public class CreditCard {

    private String cardNumber;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private String cardCVV2;
    private String amount;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiryMonth() {
        return cardExpiryMonth;
    }

    public void setCardExpiryMonth(String cardExpiryMonth) {
        this.cardExpiryMonth = cardExpiryMonth;
    }

    public String getCardExpiryYear() {
        return cardExpiryYear;
    }

    public void setCardExpiryYear(String cardExpiryYear) {
        this.cardExpiryYear = cardExpiryYear;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardCVV2() {
        return cardCVV2;
    }

    public void setCardCVV2(String cardCVV2) {
        this.cardCVV2 = cardCVV2;
    }
}