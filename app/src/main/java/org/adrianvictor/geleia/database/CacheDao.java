package org.adrianvictor.geleia.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.adrianvictor.geleia.model.Song;

import java.util.List;

@Dao
public interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCache(Cache cache);

    @Query("SELECT * FROM cache")
    List<Cache> getAll();

    @Query("SELECT * FROM songs LEFT JOIN cache USING(id) WHERE songs.id IN (:ids)")
    List<Song> getSongs(List<String> ids);

    @Query("SELECT cache FROM cache WHERE id = :id")
    boolean isCached(String id);
}
