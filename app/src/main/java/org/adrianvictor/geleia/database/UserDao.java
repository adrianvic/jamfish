package org.adrianvictor.geleia.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.adrianvictor.geleia.model.User;

import java.util.List;
import java.util.UUID;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT * FROM users WHERE uuid = :id")
    User getUser(UUID id);
}
