package spotify.errorhandling.customexceptions;

public class CollectionNotFoundException extends RuntimeException{
    public CollectionNotFoundException(String message) {
        super(message);
    }
}
