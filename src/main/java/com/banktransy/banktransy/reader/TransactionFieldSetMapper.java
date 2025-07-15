package com.banktransy.banktransy.reader;

import com.banktransy.banktransy.model.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {
    //
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) {
        Transaction t = new Transaction();
        try {
            t.setTime_transaction(LocalDateTime.parse(fieldSet.readString("time_transaction"), formatter));
        } catch (DateTimeParseException e) {
            System.err.println("❌ Erreur de parsing pour le champ 'time_transaction': " + fieldSet.readString("time_transaction"));
            throw e;
        }

        t.setFromBank(fieldSet.readString("fromBank"));
        t.setSourceAccount(fieldSet.readString("sourceAccount"));
        t.setToBank(fieldSet.readString("toBank"));
        t.setDestinationAccount(fieldSet.readString("destinationAccount"));
        t.setAmount_received(fieldSet.readDouble("amount_received"));
        t.setReceiving_currency(fieldSet.readString("receiving_currency"));
        t.setAmount_paid(fieldSet.readDouble("amount_paid"));
        t.setPayment_currency(fieldSet.readString("payment_currency"));
        t.setPayment_format(fieldSet.readString("payment_format"));

        return t;
}
}
