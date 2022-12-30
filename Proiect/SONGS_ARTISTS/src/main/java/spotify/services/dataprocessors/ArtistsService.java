package spotify.services.dataprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import spotify.utils.errorhandling.customexceptions.ConflictException;
import spotify.utils.errorhandling.customexceptions.EntityNotFoundException;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.model.repos.ArtistsRepository;
import spotify.services.validators.CreateValidator;
import spotify.utils.errorhandling.ErrorMessages;

import java.util.*;
import java.util.stream.Collectors;

// SQL constraints validation is part of this service
@Component
public class ArtistsService {
    @Autowired
    private ArtistsRepository artistsRepository;

    @Autowired
    private CreateValidator createValidator;

    public Set<ArtistEntity> getAllArtists() {
        return new HashSet<>(artistsRepository.findAll());
    }

    public Page<ArtistEntity> getPageableArtists(Integer page, Integer pageSize, String name, String match) {
        Pageable paging;

        if (page == null) {
            paging = PageRequest.ofSize(pageSize);
        } else {
            paging = PageRequest.of(page, pageSize);
        }

        if (name == null) {
            return artistsRepository.findAll(paging);
        } else {
            if (match == null) {
                return artistsRepository.findAllByNameContaining(name, paging);
            } else {
                return artistsRepository.findAllByName(name, paging);
            }
        }
    }

    public ArtistEntity getArtistById(int uuid) {
        Optional<ArtistEntity> artistEntity = artistsRepository.findById(uuid);

        if (!artistEntity.isPresent()) {
            throw new EntityNotFoundException(ErrorMessages.ARTIST_NOT_FOUND + uuid);
        }

        return artistEntity.get();
    }

    public ArtistEntity getArtistByName(String name) {
        ArtistEntity artistEntity = artistsRepository.findByName(name);

        if (artistEntity == null) {
            throw new EntityNotFoundException(ErrorMessages.ARTIST_NOT_FOUND + name);
        }

        return artistEntity;
    }

    public boolean itExistsArtist(int uuid) {
        return artistsRepository.findById(uuid).isPresent();
    }

    public ArtistEntity createOrReplaceArtist(ArtistEntity artistEntity) {
        // verificare suplimentara doar pt a avea mesaj custom la exceptie; altfel, e returnat in response mesajul exceptiei din bd
        if (artistsRepository.findByName(artistEntity.getName()) != null) {
            throw new ConflictException(artistEntity.getName() + ErrorMessages.NAME_ALREADY_EXISTENT);
        }

        return artistsRepository.save(artistEntity);
    }

    public void deleteArtist(ArtistEntity artistEntity) {

        artistsRepository.delete(artistEntity);
    }

    public ArtistEntity addSongsToArtist(ArtistEntity artistEntity, Set<SongEntity> songEntities) {
        Set<SongEntity> newSongsSet = artistEntity.getSongs();
        newSongsSet.addAll(songEntities);

        artistEntity.setSongs(newSongsSet);
        return artistsRepository.save(artistEntity);
    }

    public void assignSongToMultipleArtists(Set<ArtistEntity> artistEntities, SongEntity songEntity){
        artistEntities.forEach(artistEntity -> addSongsToArtist(artistEntity, new HashSet<>() {{
            add(songEntity);
        }}));
    }

    public Set<ArtistEntity> getArtistsByName(Set<String> artistNames){
        Set<ArtistEntity> artistEntities = new HashSet<>();

        // if at least one artist doesn't exist, the join table will not be updated
        for (String artistName : artistNames) {
            artistEntities.add(getArtistByName(artistName));
        }

        return artistEntities;
    }

    public void removeSongFromArtists(int songId) {
        List<ArtistEntity> artistEntities = artistsRepository.artistsForGivenSong(songId);

        for (ArtistEntity a : artistEntities) {
            Set<SongEntity> remainedSongs = a.getSongs().stream().filter(s -> s.getId() != songId).collect(Collectors.toSet());
            a.setSongs(remainedSongs);
            artistsRepository.save(a);
        }
    }

    public Set<ArtistEntity> getArtistForGivenSong(int songId) {
        List<ArtistEntity> artistEntities = artistsRepository.artistsForGivenSong(songId);
        if(artistEntities.size() == 0){
            // because a song cannot be created without artists
            throw new EntityNotFoundException(ErrorMessages.SONG_NOT_FOUND + songId);
        }
        return new HashSet<>(artistEntities);
    }

}
