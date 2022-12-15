package spotify.services.validators.entity;

import org.springframework.stereotype.Component;
import spotify.errorhandling.customexceptions.UnprocessableContentException;
import spotify.errorhandling.utils.ErrorMessages;
import spotify.model.entities.enums.MusicGenre;

@Component
public class FilterValidator implements Validator {
    @Override
    public void validate(Object target, String... dependency) {
        switch (dependency[0]) {
            case "title":
                validateTitle(target);
                break;
            case "year":
                validateYear(target);
                break;
            case "genre":
                validateGenre(target);
                break;
        }
    }

    private void validateTitle(Object title) {

    }

    private void validateYear(Object year) {
        try {
            int y = Integer.parseInt(year.toString());
            if (y < 0 || y >= 4000) {
                throw new UnprocessableContentException(ErrorMessages.INVALID_YEAR);
            }
        } catch (IllegalArgumentException ex) {
            throw new UnprocessableContentException(ErrorMessages.INVALID_YEAR);
        }
    }

    private void validateGenre(Object genre) {
        try {
            MusicGenre mg = MusicGenre.valueOf(genre.toString().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new UnprocessableContentException(ErrorMessages.INVALID_MUSIC_GENRE);
        }
    }
}