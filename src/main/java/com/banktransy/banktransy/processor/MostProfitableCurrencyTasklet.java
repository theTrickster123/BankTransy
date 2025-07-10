package com.banktransy.banktransy.processor;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@StepScope
public class MostProfitableCurrencyTasklet implements Tasklet {

    @Value("#{jobParameters['output.final']}")
    private String filePath;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        Map<String, BigDecimal> currencyProfitMap = new HashMap<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String currency = parts[2];
                BigDecimal difference = new BigDecimal(parts[3]);
                currencyProfitMap.put(currency, difference);
            }
        }

        Map.Entry<String, BigDecimal> minEntry = currencyProfitMap.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        String resultMessage;

        if (minEntry != null) {
            resultMessage="La Devise la plus rentable pour la banque est : "
                    + minEntry.getKey() + " avec un profit de : " + minEntry.getValue();
        } else {
            resultMessage="Aucune donnée trouvée.";
        }

        Files.write(path, List.of(resultMessage), StandardOpenOption.APPEND);

        return RepeatStatus.FINISHED;
}
}
