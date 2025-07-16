package com.banktransy.banktransy.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;


@Component
public class FakeTransactionTasklet  implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(FakeTransactionTasklet.class);
    private final JdbcTemplate jdbcTemplate;

    public FakeTransactionTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String message ="Input file not found: inserting a fake transaction";
        String sql = """
    INSERT INTO transaction (
        time_transaction,
        from_bank,
        from_account,
        to_bank,
        to_account,
        amount_received,
        receiving_currency,
        amount_paid,
        payment_currency,
        payment_format,
        errors                    
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)
""";

        jdbcTemplate.update(sql,
                Timestamp.from(Instant.now()), // identify sql timestamp for jdbc api
                "", "", "", "",
                0.0, "",
                0.0, "", "",message);

        return RepeatStatus.FINISHED;
    }
}
