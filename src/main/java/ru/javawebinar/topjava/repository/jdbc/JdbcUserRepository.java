package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
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
import ru.javawebinar.topjava.util.ValidationUtil;

import javax.validation.Validator;
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
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    private Validator validator;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        ValidationUtil.validate(validator, user);

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        Integer userId;
        if (user.isNew()) {
            userId = (Integer) insertUser.executeAndReturnKey(parameterSource);
            user.setId(userId);
        } else {
            if (namedParameterJdbcTemplate.update(
                    """
                            UPDATE users SET name = :name, email = :email, password = :password, registered = :registered, 
                                             enabled = :enabled, calories_per_day = :caloriesPerDay 
                             WHERE id = :id
                            """, parameterSource) == 0) {
                return null;
            }
            userId = user.getId();
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
        }
        if (!user.getRoles().isEmpty()) {
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
                               LEFT JOIN user_roles AS ur ON u.id = ur.user_id 
                         WHERE id = ?
                        """, getRowMapper(), id);
        return DataAccessUtils.singleResult(removeDuplicates(users));
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                """
                        SELECT * 
                          FROM users AS u
                               LEFT JOIN user_roles AS ur ON u.id = ur.user_id 
                         WHERE email = ?
                        """, getRowMapper(), email);
        return DataAccessUtils.singleResult(removeDuplicates(users));
    }


    @Override
    public List<User> getAll() {
        Set<User> users = new LinkedHashSet<>(jdbcTemplate.query(
                """
                          SELECT *
                            FROM users AS u
                                 LEFT JOIN user_roles AS ur ON u.id = ur.user_id 
                        ORDER BY name, email  
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
                String roleName = rs.getString("role");
                if (roleName != null) {
                    user.getRoles().add(Role.valueOf(roleName));
                }
                return user;
            }
        };
    }

    private List<User> removeDuplicates(List<User> users) {
        return users.stream().distinct().collect(Collectors.toList());
    }
}
