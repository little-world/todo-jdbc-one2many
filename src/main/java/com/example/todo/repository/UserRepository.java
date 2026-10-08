package com.example.todo.repository;

import com.example.todo.model.Todo;
import com.example.todo.model.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Loads a user together with their todos (todo.user_id is the FK). */
    public Optional<User> findById(Long userId) {
        try (Connection connection = dataSource.getConnection()) {
            User user;
            try (PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM todo_user WHERE id = ?")) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    user = new User(rs.getLong("id"), rs.getString("name"));
                }
            }

            List<Todo> todos = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement("SELECT id, task FROM todo WHERE user_id = ?")) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        todos.add(new Todo(rs.getLong("id"), rs.getString("task")));
                    }
                }
            }
            user.setTodoList(todos);
            return Optional.of(user);

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load user " + userId, ex);
        }
    }

    public void save(User user) {
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
    }

    public void addTodo(Long userId, Todo todo) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO todo (task, user_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, todo.getTask());
            ps.setLong(2, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    todo.setId(keys.getLong(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add todo for user " + userId, ex);
        }
    }
}
