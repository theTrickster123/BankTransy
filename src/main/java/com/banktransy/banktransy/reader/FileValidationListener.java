package com.banktransy.banktransy.reader;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@StepScope
public class FileValidationListener implements StepExecutionListener {

    @Value("#{jobParameters['input.file']}")
    private String inputFile;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (!Files.exists(Path.of(inputFile))) {
            stepExecution.setStatus(BatchStatus.FAILED);
            stepExecution.setExitStatus(new ExitStatus("MISSING_FILE"));
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus(); // ne rien changer
    }

}
