package spotify.exceptions;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(int id) {
        super("Could not find song " + id);
    }
}
