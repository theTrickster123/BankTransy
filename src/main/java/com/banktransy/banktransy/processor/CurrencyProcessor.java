package com.banktransy.banktransy.processor;

import com.banktransy.banktransy.model.CurrencyProfitable;
import com.banktransy.banktransy.model.FinalResult;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrencyProcessor implements ItemProcessor<CurrencyProfitable, FinalResult> {

    @Override
    public FinalResult process(CurrencyProfitable currencyProfitable) throws  Exception{
        BigDecimal received=currencyProfitable.getSumReceivedAmount();
        BigDecimal paid=currencyProfitable.getSumPaimentAmount();
        BigDecimal difference=received.subtract(paid);

       return new FinalResult(currencyProfitable,difference);

    }



}
