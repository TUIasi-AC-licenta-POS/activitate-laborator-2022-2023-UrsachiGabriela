package spotify.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import spotify.errorhandling.customexceptions.CollectionNotFoundException;
import spotify.errorhandling.customexceptions.ConflictException;
import spotify.errorhandling.customexceptions.DocumentNotFoundException;
import spotify.errorhandling.utils.ErrorMessages;
import spotify.model.collections.Playlist;
import spotify.model.collections.Resource;
import spotify.model.repos.PlaylistRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Set<Playlist> getAllPlaylists(String userId, String playlistName) {
        String collectionName = String.format("user.%s.playlists", userId);

        if (!mongoTemplate.collectionExists(collectionName)) {
            throw new CollectionNotFoundException(ErrorMessages.COLLECTION_NOT_FOUND);
        }

        playlistRepository.setCollectionName(collectionName);

        if (playlistName == null) {
            return new HashSet<>(playlistRepository.findAll());
        }

        List<Playlist> playlists = playlistRepository.findAllByNameContaining(playlistName);
        if (playlists.isEmpty()) {
            throw new CollectionNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + playlistName);
        }
        return new HashSet<>(playlists);

    }

    public Playlist getPlaylistById(String userId, String id) {
        String collectionName = String.format("user.%s.playlists", userId);

        if (!mongoTemplate.collectionExists(collectionName)) {
            throw new CollectionNotFoundException(ErrorMessages.COLLECTION_NOT_FOUND);
        }

        playlistRepository.setCollectionName(collectionName);

        Optional<Playlist> playlist = playlistRepository.findById(id);

        if (playlist.isEmpty()) {
            throw new CollectionNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + id);
        }

        return playlist.get();
    }

    public Playlist createPlaylist(String userId, Playlist playlist) {
        String collectionName = String.format("user.%s.playlists", userId);

        playlistRepository.setCollectionName(collectionName);

        // daca exista deja un playlist cu acelasi nume (UK pe nume playlist)
        if (mongoTemplate.collectionExists(collectionName) && playlistRepository.existsByName(playlist.getName())) {
            throw new ConflictException(ErrorMessages.PLAYLIST_ALREADY_EXISTS);
        }

        return playlistRepository.save(playlist);
    }

    public Playlist addSongToPlaylist(String userId, String id, Resource resource) {
        String collectionName = String.format("user.%s.playlists", userId);
        playlistRepository.setCollectionName(collectionName);

        if (!mongoTemplate.collectionExists(collectionName)) {
            throw new CollectionNotFoundException(ErrorMessages.COLLECTION_NOT_FOUND);
        }

        Optional<Playlist> playlist = playlistRepository.findById(id);

        if (playlist.isEmpty()) {
            throw new DocumentNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + id);
        }

        playlist.get().getFavSongs().add(resource);
        return playlistRepository.save(playlist.get());
    }

//    public Playlist deleteSongFromPlaylist(String userId, String playlistId, Resource resource){
//
//    }

//    public Playlist getPlaylistByName(String userId, String playlistName) {
//        String collectionName = String.format("user.%s.playlists", userId);
//        playlistRepository.setCollectionName(collectionName);
//
//        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
//
//        if (playlist.isEmpty()) {
//            throw new EntityNotFoundException(ErrorMessages.PLAYLIST_NOT_FOUND + playlistName);
//        }
//
//        return playlist.get();
//    }

    public void savePlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }


}
