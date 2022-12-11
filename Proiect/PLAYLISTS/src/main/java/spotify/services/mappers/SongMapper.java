package spotify.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import spotify.model.collections.Resource;
import spotify.view.requests.SongRequest;

@Mapper
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);


    default Resource toSongResource(SongRequest songRequest){
        if ( songRequest == null ) {
            return null;
        }

        Resource resource = new Resource();

        resource.setResourceId( songRequest.getId() );
        resource.setName( songRequest.getName() );
        resource.setLink(songRequest.getLink("self").get().getHref());

        return resource;
    }
}
