package com.example.todo.repository;

import com.example.todo.model.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserRepository {

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Loads the user row only; todos are merged in by the UserService. */
    public Optional<User> findById(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM todo_user WHERE id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new User(rs.getLong("id"), rs.getString("name")));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load user " + userId, ex);
        }
    }

    public User save(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO todo_user (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save user", ex);
        }
        return user;
    }
}
