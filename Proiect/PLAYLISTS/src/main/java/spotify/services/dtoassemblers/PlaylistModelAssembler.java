package spotify.services.dtoassemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import spotify.controller.WebController;
import spotify.view.responses.PlaylistResponse;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlaylistModelAssembler extends RepresentationModelAssemblerSupport<PlaylistResponse, PlaylistResponse> {


    public PlaylistModelAssembler() {
        super(WebController.class, PlaylistResponse.class);
    }

    @Override
    public PlaylistResponse toModel(PlaylistResponse playlistResponse) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(
                methodOn(WebController.class).getPlaylistById(playlistResponse.getId())
        )
                .withRel("self"));
        links.add(linkTo(
                methodOn(WebController.class).getAllPlaylists(null))
                .withRel("parent"));

        playlistResponse.add(links);

        return playlistResponse;
    }

    @Override
    public CollectionModel<PlaylistResponse> toCollectionModel(Iterable<? extends PlaylistResponse> playlistDTOS) {
        CollectionModel<PlaylistResponse> newPlaylistDTOS = super.toCollectionModel(playlistDTOS);
        newPlaylistDTOS.add(linkTo(methodOn(WebController.class).getAllPlaylists(null)).withSelfRel());

        return newPlaylistDTOS;
    }
}
