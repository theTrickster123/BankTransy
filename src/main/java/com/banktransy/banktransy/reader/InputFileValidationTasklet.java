package com.banktransy.banktransy.reader;

import com.banktransy.banktransy.ErrorPersistenceService;
import com.banktransy.banktransy.exception.CustomMissingParameterException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@StepScope
public class InputFileValidationTasklet implements Tasklet {

    @Value("${job.input.file.default}")
    private String defaultInputFile;

    private final ErrorPersistenceService errorPersistenceService;

    public InputFileValidationTasklet(ErrorPersistenceService errorPersistenceService) {
        this.errorPersistenceService = errorPersistenceService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String inputFile = (String) chunkContext.getStepContext().getJobParameters().get("input.file");

        if (inputFile == null || inputFile.trim().isEmpty()) {
            inputFile = defaultInputFile;
        }

        if ("empty".equalsIgnoreCase(inputFile.trim())) {
            errorPersistenceService.insertError("Le paramètre 'input.file' est manquant ou vide !");
            throw new CustomMissingParameterException("Le paramètre 'input.file' est manquant ou vide !");
        }

        return RepeatStatus.FINISHED;
    }
}
