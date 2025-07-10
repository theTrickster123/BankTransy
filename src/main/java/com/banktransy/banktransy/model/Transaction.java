package com.banktransy.banktransy.model;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {

    String time_transaction;
    String fromBank;
    String sourceAccount;
    String toBank;
    String destinationAccount;
    double amount_received;
    String receiving_currency;
    double amount_paid;
    String payment_currency;
    String payment_format;


}
