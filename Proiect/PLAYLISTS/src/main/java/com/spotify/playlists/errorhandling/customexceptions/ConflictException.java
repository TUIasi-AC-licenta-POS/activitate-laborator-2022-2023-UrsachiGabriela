package com.spotify.playlists.errorhandling.customexceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
