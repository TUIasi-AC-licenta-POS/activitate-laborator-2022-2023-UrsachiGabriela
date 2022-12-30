package spotify.services.dataprocessors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import spotify.utils.errorhandling.customexceptions.ConflictException;
import spotify.utils.errorhandling.customexceptions.EntityNotFoundException;
import spotify.model.entities.SongEntity;
import spotify.model.repos.SongsRepository;
import spotify.services.validators.CreateValidator;
import spotify.services.validators.FilterValidator;
import spotify.utils.errorhandling.ErrorMessages;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SongsService {

    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private CreateValidator createValidator;
    @Autowired
    private FilterValidator filterValidator;
    @Autowired
    private ArtistsService artistsService;

    public Set<SongEntity> getAllSongs() {
        return new HashSet<>(songsRepository.findAll());
    }

    public Page<SongEntity> getPageableSongs2(Integer page, Integer pageSize, String searchBy, String searchedValue, String match) {
        Pageable paging = getPageable(page, pageSize);

        if (searchBy == null || searchedValue == null) {
            // display all without conditions
            return getPageableSongs(paging);

        } else {
            // validate type of searched value for given searchedBy param
            filterValidator.validate(searchedValue, searchBy);

            return searchBy.equals("title") ? (getPageableSongsByTitle(paging, searchedValue, match))
                    : (searchBy.equals("year") ? getPageableSongsByYear(paging, Integer.parseInt(searchedValue))
                    : getPageableSongsByGenre(paging, MusicGenre.valueOf(searchedValue.toUpperCase())));

        }
    }

    private Page<SongEntity> getPageableSongs(Pageable paging) {
        return songsRepository.findAll(paging);
    }

    private Page<SongEntity> getPageableSongsByTitle(Pageable paging, String title, String match) {
        return match != null ? songsRepository.findAllByName(title, paging) : songsRepository.findAllByNameContaining(title, paging);
    }

    private Page<SongEntity> getPageableSongsByYear(Pageable paging, Integer year) {
        return songsRepository.findAllByYear(year, paging);

    }

    private Page<SongEntity> getPageableSongsByGenre(Pageable paging, MusicGenre musicGenre) {
        return songsRepository.findAllByGenre(musicGenre, paging);
    }

    public SongEntity getSongById(int sid) {
        Optional<SongEntity> songEntity = songsRepository.findById(sid);

        if (!songEntity.isPresent()) {
            throw new EntityNotFoundException(ErrorMessages.SONG_NOT_FOUND + sid);
        }
        return songEntity.get();
    }

    public SongEntity getAlbumById(int albumId) {
        Optional<SongEntity> songEntity = songsRepository.findById(albumId);

        if (!songEntity.isPresent()) {
            throw new ConflictException(ErrorMessages.INEXISTENT_ALBUM + albumId);
        }
        return songEntity.get();
    }

    public SongEntity createNewSong(SongEntity songEntity) {
        createValidator.validate(songEntity);
        return songsRepository.save(songEntity);
    }

    public void deleteSong(SongEntity songEntity) {

        if (songEntity.getType().equals(MusicType.ALBUM) && !songEntity.getSongEntities().isEmpty()) {
            throw new ConflictException("You are not able to remove this album until you remove all its songs");
        }

        artistsService.removeSongFromArtists(songEntity.getId());
        songsRepository.deleteById(songEntity.getId());
    }


    private Pageable getPageable(Integer page, Integer pageSize) {
        Pageable paging;

        if (page == null) {
            paging = PageRequest.ofSize(pageSize);
        } else {
            paging = PageRequest.of(page, pageSize);
        }

        return paging;
    }

}
