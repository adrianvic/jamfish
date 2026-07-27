package org.adrianvictor.geleia.database;

import androidx.room.TypeConverter;

import java.util.UUID;

public class Converters {
    @TypeConverter
    public static String fromUuid(UUID uuid) {
        return uuid == null ? null : uuid.toString();   // store as TEXT
    }

    @TypeConverter
    public static UUID toUuid(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
