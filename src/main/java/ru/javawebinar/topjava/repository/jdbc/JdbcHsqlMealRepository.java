package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Profile("hsqldb")
@Repository
public class JdbcHsqlMealRepository extends JdbcMealRepository {
    @Autowired
    public JdbcHsqlMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T convertDateTime(LocalDateTime dateTime) {
        return (T) java.sql.Timestamp.valueOf(dateTime);
    }
}
