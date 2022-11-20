package spotify.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spotify.exceptions.SongNotFoundException;
import spotify.model.entities.SongEntity;
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

    public SongEntity getSongById(int sid) {
        Optional<SongEntity> songEntity = songsRepository.findById(sid);

        if (songEntity.isEmpty()) {
            throw new SongNotFoundException(sid);
        }
        return songEntity.get();
    }
}
