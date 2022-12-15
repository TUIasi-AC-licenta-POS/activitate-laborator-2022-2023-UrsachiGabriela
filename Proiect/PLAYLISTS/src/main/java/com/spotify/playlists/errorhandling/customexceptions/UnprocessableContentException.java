package com.spotify.playlists.errorhandling.customexceptions;

public class UnprocessableContentException extends RuntimeException {
    public UnprocessableContentException(String message) {
        super(message);
    }
}
