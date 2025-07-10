package com.banktransy.banktransy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReport {
    Transaction transaction;
    String is_profitableCurrency;
}
