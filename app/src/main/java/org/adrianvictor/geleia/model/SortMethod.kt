package org.adrianvictor.geleia.model

enum class SortMethod(val api: String) {
    NAME("SortName"),
    ALBUM("Album"),
    ARTIST("AlbumArtist"),
    YEAR("ProductionYear"),
    ADDED("DateCreated"),
    RANDOM("Random"),
    COUNT("PlayCount");
}
