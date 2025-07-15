package com.banktransy.banktransy.reader;

import com.banktransy.banktransy.exception.CustomMissingParameterException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InputFileValidationTasklet implements Tasklet {

    @Value("${job.input.file.default}")
    private String defaultInputFile;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String inputFile = (String) chunkContext.getStepContext().getJobParameters().get("input.file");

        if (inputFile == null || inputFile.trim().isEmpty()) {
            inputFile = defaultInputFile;
        }

        if ("empty".equalsIgnoreCase(inputFile.trim())) {
            throw new CustomMissingParameterException("Le paramètre 'input.file' est manquant ou vide !");
        }

        // Sinon continuer normalement
        return RepeatStatus.FINISHED;
    }
}
