package com.banktransy.banktransy.config;

import com.banktransy.banktransy.model.CurrencyProfitable;
import com.banktransy.banktransy.model.FinalResult;
import com.banktransy.banktransy.model.Transaction;
import com.banktransy.banktransy.model.TransactionReport;
import com.banktransy.banktransy.processor.CurrencyProcessor;
import com.banktransy.banktransy.processor.FilterProcessor;
import com.banktransy.banktransy.processor.MostProfitableCurrencyTasklet;
import com.banktransy.banktransy.reader.FakeTransactionTasklet;
import com.banktransy.banktransy.reader.FileValidationListener;
import com.banktransy.banktransy.reader.InputFileValidationTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfig {
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager
    , ItemReader<Transaction> transactionreader,
                      ItemWriter<Transaction> jdbcBatchItemWriter) {
        return new StepBuilder("step1",jobRepository)
                .<Transaction,Transaction>chunk(100,platformTransactionManager)
                .reader(transactionreader)
                .writer(jdbcBatchItemWriter)
                .build();
    }



    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager
            , ItemReader<Transaction> transactionJdbcCursorItemReader, ItemWriter<TransactionReport>
                                  Reportwriter, ItemProcessor<Transaction,TransactionReport> filterProcessor) {
        return new StepBuilder("Step2",jobRepository)
                .<Transaction,TransactionReport>chunk(100,platformTransactionManager)
                .reader(transactionJdbcCursorItemReader)
                .writer(Reportwriter)
                .processor(filterProcessor)
                .build();
    }

    @Bean

    public Step step3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager
            ,@Qualifier("jdbcTransactionCursorItemReader") ItemReader<CurrencyProfitable> jdbcCursorItemReader, ItemWriter<CurrencyProfitable> flatFileItemWriter){
    return new StepBuilder("step",jobRepository)
            .<CurrencyProfitable,CurrencyProfitable>chunk(100,platformTransactionManager)
            .reader(jdbcCursorItemReader)
            .writer(flatFileItemWriter)
            .build();
    }

    @Bean

  public Step step4(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager
            ,  @Qualifier("resultItemReader") ItemReader<CurrencyProfitable> reader, ItemWriter<FinalResult> resultItemWriter
            , ItemProcessor<CurrencyProfitable,FinalResult> resultprocessor) {
        return new StepBuilder("Step4",jobRepository)
                .<CurrencyProfitable,FinalResult>chunk(1,platformTransactionManager)
                .reader(reader)
                .processor(resultprocessor)
                .writer(resultItemWriter)
                .build();



    }
    @Bean
    public Step step5(JobRepository jobRepository,  @Qualifier("myTasklet") Tasklet tasklet, PlatformTransactionManager transactionManager){
        return new StepBuilder("step5",jobRepository)
                .tasklet(tasklet,transactionManager)
                .build();
    }
    @Bean
    public Step checkInputParameterStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        InputFileValidationTasklet inputFileValidationTasklet) {
        return new StepBuilder("checkInputParameterStep", jobRepository)
                .tasklet(inputFileValidationTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step validateFileStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 FileValidationListener fileValidationListener) {
        return new StepBuilder("validateFileStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .listener(fileValidationListener)
                .build();
    }

    @Bean
    public Step fakeStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         FakeTransactionTasklet fakeTransactionTasklet) {
        return new StepBuilder("fakeStep", jobRepository)
                .tasklet(fakeTransactionTasklet, transactionManager)
                .build();
    }

    @Bean
    public Flow conditionalFlow(Step checkInputParameterStep,Step validateFileStep, Step fakeStep, Step step1) {
        return new FlowBuilder<SimpleFlow>("conditionalFlow")
                .start(checkInputParameterStep) // ← Étape qui vérifie le paramètre
                .next(validateFileStep)         // ← Étape qui vérifie l'existence du fichier
                .on("MISSING_FILE").to(fakeStep)
                .from(validateFileStep)
                .on("*").to(step1)
                .build();
    }
    @Bean
    public Flow postConditionalFlow(Step step2, Step step3, Step step4, Step step5) {
        return new FlowBuilder<SimpleFlow>("postConditionalFlow")
                .start(step2)
                .next(step3)
                .next(step4)
                .next(step5)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository,
                   Flow conditionalFlow,
                   Flow postConditionalFlow) {

        return new JobBuilder("TransactionBankJob", jobRepository)
                .start(conditionalFlow)
                .next(postConditionalFlow)
                .end()
                .build();
    }

    @Bean
    @StepScope
    public FilterProcessor filterProcessor(@Value("${plafond.eur}") double plafondEur,
                                           @Value("${plafond.usd}") double plafondUsd,
                                           @Value("${plafond.mad}") double plafondMad,
                                           @Value("${plafond.default}") double plafondDefault){
        return new FilterProcessor(plafondEur, plafondUsd, plafondMad, plafondDefault);
    }

    @Bean
    @StepScope
    public CurrencyProcessor currencyProcessor(){
        return new CurrencyProcessor();
    }

    @Bean
    @StepScope
    public Tasklet myTasklet(){
        return new MostProfitableCurrencyTasklet();
    }


}
