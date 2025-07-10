package com.banktransy.banktransy.writer;


import com.banktransy.banktransy.model.CurrencyProfitable;
import com.banktransy.banktransy.model.FinalResult;
import com.banktransy.banktransy.model.Transaction;
import com.banktransy.banktransy.model.TransactionReport;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
public class ReportWriter {
    @Bean
    @StepScope
public FlatFileItemWriter<TransactionReport> Reportwriter(@Value("#{jobParameters['output.file']}") String outputFile) {
      return new FlatFileItemWriterBuilder<TransactionReport>()
              .name("ReportDatawriter")
              .resource(new FileSystemResource(outputFile))
              .delimited()
              .names("transaction.time_transaction", "transaction.fromBank", "transaction.sourceAccount", "transaction.toBank", "transaction.destinationAccount",
                      "transaction.amount_received", "transaction.receiving_currency", "transaction.amount_paid", "transaction.payment_currency", "transaction.payment_format","is_profitableCurrency")

              .build();
    }
  @Bean
  @StepScope
  public JdbcBatchItemWriter<Transaction> jdbcBatchItemWriter(DataSource dataSource) {
      String sql = "INSERT INTO transaction (time_transaction, from_bank, from_account, to_bank, to_account, " +
              "amount_received, receiving_currency, amount_paid, payment_currency, payment_format) " +
              "VALUES (:time_transaction, :fromBank, :sourceAccount, :toBank, :destinationAccount, " +
              ":amount_received, :receiving_currency, :amount_paid, :payment_currency, :payment_format)";

      return new JdbcBatchItemWriterBuilder<Transaction>()
              .dataSource(dataSource)
              .sql(sql)
              .beanMapped()
              .build();
  }

  @Bean
    @StepScope
    public FlatFileItemWriter<CurrencyProfitable>  flatFileItemWriter(@Value("#{jobParameters['outputCurrency.file']}") String outputCurrencyFile) {
        return new FlatFileItemWriterBuilder<CurrencyProfitable>()
                .name("flatfileCurrencywriter")
                .resource(new FileSystemResource(outputCurrencyFile))
                .delimited()
                .names( "sumPaimentAmount" , "sumReceivedAmount","currency")
                .build();
  }
    @Bean
    @StepScope
    public FlatFileItemWriter<FinalResult> finalResultWriter(@Value("#{jobParameters['outputResultfinal.file']}") String outputPath) {
        return new FlatFileItemWriterBuilder<FinalResult>()
                .name("finalResultWriter")
                .resource(new FileSystemResource(outputPath))
                .delimited()
                .names("currencyProfitable.sumPaimentAmount" , "currencyProfitable.sumReceivedAmount","currencyProfitable.currency", "difference")
                .build();
    }

}
