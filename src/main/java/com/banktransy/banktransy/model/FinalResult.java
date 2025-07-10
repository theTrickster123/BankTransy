package com.banktransy.banktransy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinalResult {
    CurrencyProfitable currencyProfitable;
    BigDecimal difference;
}
