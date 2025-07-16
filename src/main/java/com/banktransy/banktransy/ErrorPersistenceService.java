package com.banktransy.banktransy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class ErrorPersistenceService {

    private final JdbcTemplate jdbcTemplate;

    public ErrorPersistenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertError(String message) {
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
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                Timestamp.from(Instant.now()),
                "", "", "", "",
                0.0, "",
                0.0, "", "",
                message
        );
    }
}
