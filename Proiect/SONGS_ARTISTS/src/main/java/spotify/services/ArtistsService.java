package spotify.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spotify.exceptions.ArtistNotFoundException;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.model.repos.ArtistsRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ArtistsService {
    @Autowired
    private ArtistsRepository artistsRepository;


    public Set<ArtistEntity> getAllArtists() {
        return new HashSet<>(artistsRepository.findAll());
    }

    public ArtistEntity getArtistById(int uuid) {
        Optional<ArtistEntity> artistEntity = artistsRepository.findById(uuid);

        if (artistEntity.isEmpty()) {
            throw new ArtistNotFoundException(uuid);
        }
        return artistEntity.get();
    }

    public boolean itExistsArtist(int uuid) {
        return artistsRepository.findById(uuid).isPresent();
    }

//    public Set<SongEntity> getSongsForArtistId(int uuid) {
//        Optional<ArtistEntity> artistEntity = artistsRepository.findById(uuid);
//
//        if (artistEntity.isEmpty()) {
//            throw new ArtistNotFoundException(uuid);
//        }
//        return artistEntity.get().getSongs();
//    }

    public ArtistEntity createNewArtist(ArtistEntity artistEntity) {
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

}
