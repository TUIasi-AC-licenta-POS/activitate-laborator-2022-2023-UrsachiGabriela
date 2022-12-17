package spotify.services.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import spotify.model.entities.ArtistEntity;
import spotify.view.requests.NewArtistRequest;
import spotify.view.responses.ArtistDTO;

import java.util.Set;

@Mapper(uses = {SongMapper.class})
public interface ArtistMapper {
    ArtistMapper INSTANCE = Mappers.getMapper(ArtistMapper.class);

    @Named("completeArtistMapper")
    @Mapping(target = "songs", qualifiedByName = "simpleSongDtoSet")
    @Mapping(target = "hasSongs", expression = "java(artistEntity.getSongs().isEmpty() ? false : true)")
    ArtistDTO toCompleteArtistDto(ArtistEntity artistEntity);

    @Named("artistWithSongs")
    default ArtistDTO toArtistWithSongsDto(ArtistEntity artistEntity) {
        return ArtistDTO.builder()
                .id(artistEntity.getId())
                .hasSongs(!artistEntity.getSongs().isEmpty())
                .songs(SongMapper.INSTANCE.toSimpleSongDTOSet(artistEntity.getSongs()))
                .build();
    }

    @Named("artistWithoutSongs")
    default ArtistDTO toArtistWithoutSongsDto(ArtistEntity artistEntity) {
        return ArtistDTO.builder()
                .id(artistEntity.getId())
                .name(artistEntity.getName())
                .active(artistEntity.isActive())
                .hasSongs(!artistEntity.getSongs().isEmpty())
                .build();
    }

    @Named("artistWithName")
    default ArtistDTO toArtistWithName(ArtistEntity artistEntity) {
        return ArtistDTO.builder()
                .id(artistEntity.getId())
                .name(artistEntity.getName())
                .build();
    }

    ArtistEntity toArtistEntity(ArtistDTO artistDTO);

    ArtistEntity toArtistEntity(NewArtistRequest newArtistRequest);

    @IterableMapping(qualifiedByName = "completeArtistMapper")
    Set<ArtistDTO> toCompleteArtistDTOSet(Set<ArtistEntity> artistEntities);

    @IterableMapping(qualifiedByName = "artistWithoutSongs")
    Set<ArtistDTO> toArtistDTOWithoutSongsSet(Set<ArtistEntity> artistEntities);

    @IterableMapping(qualifiedByName = "artistWithSongs")
    Set<ArtistDTO> toArtistDTOWithSongsSet(Set<ArtistEntity> artistEntities);

    @IterableMapping(qualifiedByName = "artistWithName")
    Set<ArtistDTO> toArtistWithName(Set<ArtistEntity> artistEntities);
}
