package com.banktransy.banktransy.processor;

import com.banktransy.banktransy.model.CurrencyProfitable;
import com.banktransy.banktransy.model.Transaction;
import com.banktransy.banktransy.model.TransactionReport;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;





public class FilterProcessor implements ItemProcessor<Transaction, TransactionReport> {


    private double plafondEur;


    private double plafondUsd;


    private double plafondMad;


    private double plafondDefault;
    public FilterProcessor(double plafondEur, double plafondUsd, double plafondMad, double plafondDefault) {
        this.plafondEur = plafondEur;
        this.plafondUsd = plafondUsd;
        this.plafondMad = plafondMad;
        this.plafondDefault = plafondDefault;
    }

    @Override
    public TransactionReport process(Transaction transaction) throws Exception {
        String receivingCurrency = transaction.getReceiving_currency().toLowerCase();
        String isprofitableCurrency;
        double plafond = switch (receivingCurrency) {
            case "eur" -> plafondEur;
            case "usd" -> plafondUsd;
            case "mad" -> plafondMad;
            default -> plafondDefault;
        };

        if (transaction.getAmount_received() > plafond) {
            return null;
        }

        double conversionRate = getConversionRate(transaction.getPayment_currency(), transaction.getReceiving_currency());
        double paidInReceivingCurrency = transaction.getAmount_paid() * conversionRate;

        Boolean profitable = transaction.getAmount_received() > paidInReceivingCurrency;
        if (profitable) {
            isprofitableCurrency = transaction.getReceiving_currency().toLowerCase()+":"+"profitable";
        }
        else {isprofitableCurrency = transaction.getReceiving_currency().toLowerCase()+":"+"no-profitable";}

        return new TransactionReport(transaction, isprofitableCurrency);
    }

    private double getConversionRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return 1.0;
        if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) return 0.85;
        if (fromCurrency.equals("EUR") && toCurrency.equals("USD")) return 1.10;
        if (fromCurrency.equals("MAD") && toCurrency.equals("EUR")) return 0.091;
        if (fromCurrency.equals("EUR") && toCurrency.equals("MAD")) return 11.0;
        if (fromCurrency.equals("USD") && toCurrency.equals("MAD")) return 10.0;
        if (fromCurrency.equals("MAD") && toCurrency.equals("USD")) return 0.10;
        return 1.0;
    }



}
