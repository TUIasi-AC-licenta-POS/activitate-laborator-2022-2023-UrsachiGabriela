package spotify.services.dataprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.errorhandling.customexceptions.EntityNotFoundException;
import spotify.errorhandling.utils.ErrorMessages;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.model.repos.ArtistsRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// SQL constraints validation is part of this service
@Service
public class ArtistsService {
    @Autowired
    private ArtistsRepository artistsRepository;

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

        if (artistEntity.isEmpty()) {
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

    public ArtistEntity createNewArtist(ArtistEntity artistEntity) {

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

    public void removeSongFromArtists(int songId) {
        List<ArtistEntity> artistEntities = artistsRepository.artistsForGivenSong(songId);

        for (ArtistEntity a : artistEntities) {
            Set<SongEntity> remainedSongs = a.getSongs().stream().filter(s -> s.getId() != songId).collect(Collectors.toSet());
            a.setSongs(remainedSongs);
            artistsRepository.save(a);
        }
    }

}