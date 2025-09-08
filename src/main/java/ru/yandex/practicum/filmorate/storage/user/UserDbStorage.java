package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_ALL_FRIENDS_QUERY =
            "SELECT u.* FROM users AS u JOIN user_friends AS uf ON u.id = uf.friend_id WHERE uf.user_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(login, name, email, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE users SET login = ?, name = ?, email = ?, birthday = ?  WHERE id = ?";
    private static final String INSERT_FRIEND_QUERY =
            "INSERT INTO user_friends(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public Set<User> getUserFriends(User user) {
        List<User> friends = findMany(FIND_ALL_FRIENDS_QUERY, user.getId());
        return new HashSet<>(friends);
    }

    @Override
    public User addUser(User user) {
        long id = insert(INSERT_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(UPDATE_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(User user, long friendId) {
        update(INSERT_FRIEND_QUERY, user.getId(), friendId);
    }

    @Override
    public void deleteFriend(User user, long friendId) {
        delete(DELETE_FRIEND_QUERY, user.getId(), friendId);
    }

    @Override
    public boolean existsById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId).isPresent();
    }
}
