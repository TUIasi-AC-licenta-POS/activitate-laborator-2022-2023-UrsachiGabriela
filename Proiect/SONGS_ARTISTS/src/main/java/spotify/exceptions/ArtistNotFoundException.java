package spotify.exceptions;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(int id) {
        super("Could not find artist " + id);
    }
}
