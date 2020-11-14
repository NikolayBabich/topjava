package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final Validator validator;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            batchInsertRoles(user, (Integer) newKey);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update(
                    """
                            UPDATE users SET name = :name, email = :email, password = :password, registered = :registered, 
                                             enabled = :enabled, calories_per_day = :caloriesPerDay 
                             WHERE id = :id
                            """, parameterSource) == 0) {
                return null;
            }
            var userId = user.getId();
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
            batchInsertRoles(user, userId);
        }
        return user;
    }

    private void batchInsertRoles(User user, Integer userId) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    final List<Role> roles = new ArrayList<>(user.getRoles());

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setString(2, roles.get(i).toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return user.getRoles().size();
                    }
                });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                """
                        SELECT * 
                          FROM users AS u
                               INNER JOIN user_roles AS ur ON u.id = ur.user_id 
                         WHERE id = ?
                        """, getRowMapper(), id);
        return getSingleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                """
                        SELECT * 
                          FROM users AS u
                               INNER JOIN user_roles AS ur ON u.id = ur.user_id 
                         WHERE email = ?
                        """, getRowMapper(), email);
        return getSingleResult(users);
    }

    @Override
    public List<User> getAll() {
        Set<User> users = new LinkedHashSet<>(jdbcTemplate.query(
                """
                          SELECT *
                            FROM users AS u
                                 INNER JOIN user_roles AS ur ON u.id = ur.user_id 
                        ORDER BY email, name 
                        """, getRowMapper()));
        return new ArrayList<>(users);
    }

    private RowMapper<User> getRowMapper() {
        return new RowMapper<>() {
            final Map<Integer, User> users = new HashMap<>();

            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = users.get(rs.getInt("id"));
                if (user == null) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setRegistered(new Date(rs.getTimestamp("registered").getTime()));
                    user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                    user.setRoles(new HashSet<>());
                    users.put(user.getId(), user);
                }
                Role role = Role.valueOf(rs.getString("role"));
                user.getRoles().add(role);
                return user;
            }
        };
    }

    private User getSingleResult(List<User> users) {
        return (users.size() > 0) ? users.get(0) : null;
    }
}
