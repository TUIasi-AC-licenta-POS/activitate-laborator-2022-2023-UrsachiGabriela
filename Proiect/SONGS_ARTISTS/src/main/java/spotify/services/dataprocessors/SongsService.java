package spotify.services.dataprocessors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.utils.ErrorMessages;
import spotify.model.entities.SongEntity;
import spotify.model.entities.enums.MusicGenre;
import spotify.model.entities.enums.MusicType;
import spotify.model.repos.SongsRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SongsService {

    @Autowired
    private SongsRepository songsRepository;

    public Set<SongEntity> getAllSongs() {
        return new HashSet<>(songsRepository.findAll());
    }

    public Page<SongEntity> getPageableSongs(Integer page, Integer pageSize) {
        Pageable paging = getPageable(page, pageSize);
        return songsRepository.findAll(paging);
    }

    public Page<SongEntity> getPageableSongsByTitle(Integer page, Integer pageSize, String title, String match) {
        Pageable paging = getPageable(page, pageSize);

        return match != null ? songsRepository.findAllByName(title, paging) : songsRepository.findAllByNameContaining(title, paging);
    }

    public Page<SongEntity> getPageableSongsByYear(Integer page, Integer pageSize, Integer year) {
        Pageable paging = getPageable(page, pageSize);
        Page<SongEntity> s = songsRepository.findAllByYear(year, paging);

        return songsRepository.findAllByYear(year, paging);

    }

    public Page<SongEntity> getPageableSongsByGenre(Integer page, Integer pageSize, MusicGenre musicGenre) {
        Pageable paging = getPageable(page, pageSize);

        return songsRepository.findAllByGenre(musicGenre, paging);
    }

    public SongEntity getSongById(int sid) {
        Optional<SongEntity> songEntity = songsRepository.findById(sid);

        if (songEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessages.SONG_NOT_FOUND + sid);
        }
        return songEntity.get();
    }

    public SongEntity createNewSong(SongEntity songEntity) {
        return songsRepository.save(songEntity);
    }

    public void deleteSong(SongEntity songEntity) {

        if (songEntity.getType().equals(MusicType.ALBUM) && !songEntity.getSongEntities().isEmpty()) {
            throw new ConflictException("You are not able to remove this album until you remove all its songs");
        }
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
