package com.banktransy.banktransy.reader;

import com.banktransy.banktransy.model.CurrencyProfitable;
import com.banktransy.banktransy.model.Transaction;

import com.banktransy.banktransy.model.TransactionReport;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Configuration
public class TransactionDataReader  {
    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> transactionreader(@Value("#{jobParameters['input.file']}") String inputFile) {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionDataReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names(
                        "time_transaction", "fromBank", "sourceAccount", "toBank", "destinationAccount",
                        "amount_received", "receiving_currency", "amount_paid", "payment_currency", "payment_format"
                )
                .fieldSetMapper(new TransactionFieldSetMapper())
                .linesToSkip(1)
                .build();
    }



    @Bean


public JdbcCursorItemReader<Transaction> transactionJdbcCursorItemReader(DataSource dataSource) {
    String sql="select * from transaction";
    return new JdbcCursorItemReaderBuilder<Transaction>()
            .name("JdbcTransactionReader")
            .dataSource(dataSource)
            .sql(sql)
            .rowMapper(new DataClassRowMapper<>(Transaction.class) {})
            .build();


    }

    @Bean
    public JdbcCursorItemReader<CurrencyProfitable>  jdbcTransactionCursorItemReader(DataSource dataSource) {
        String sql="select sum(amount_paid) as sumPaimentAmount,sum(amount_received) as sumReceivedAmount,payment_currency as currency from transaction group by payment_currency ";
        return new JdbcCursorItemReaderBuilder<CurrencyProfitable>()
                .name("JdbcprofitableCurrency")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(new DataClassRowMapper<>(CurrencyProfitable.class))
                .build();
    }
//reading currencyoutput
    @Bean
    @StepScope
    public FlatFileItemReader<CurrencyProfitable> resultItemReader(@Value("#{jobParameters['inputcurrency.file']}") String inputCurrecyFile) {
        return new FlatFileItemReaderBuilder<CurrencyProfitable>()
                .name("Currecyprofitablefilereader")
                .resource(new FileSystemResource(inputCurrecyFile))
                .delimited()
                .names("sumPaimentAmount" , "sumReceivedAmount","currency")
                .targetType(CurrencyProfitable.class)
                .build();
    }





}
