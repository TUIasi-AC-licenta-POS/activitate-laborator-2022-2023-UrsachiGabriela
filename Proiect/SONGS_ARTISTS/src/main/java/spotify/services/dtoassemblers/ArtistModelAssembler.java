package spotify.services.dtoassemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import spotify.controller.WebController;
import spotify.view.responses.ArtistResponse;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ArtistModelAssembler extends RepresentationModelAssemblerSupport<ArtistResponse, ArtistResponse> {
    public ArtistModelAssembler() {
        super(WebController.class, ArtistResponse.class);
    }

    public void toSimpleModel(ArtistResponse artistResponse) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(WebController.class)
                .getArtistById(artistResponse.getId()))
                .withSelfRel());

        artistResponse.add(links);

    }

    @Override
    public ArtistResponse toModel(ArtistResponse artistResponse) {
        List<Link> links = new ArrayList<>();

        toSimpleModel(artistResponse);

        links.add(linkTo(methodOn(WebController.class).getAllArtists(null, null, null, null)).withRel("parent"));
        if (artistResponse.getHasSongs()) {
            links.add(linkTo(methodOn(WebController.class).getAllSongsForGivenArtist(artistResponse.getId())).withRel("songs"));
        }
        artistResponse.add(links);

        return artistResponse;
    }

    public void toComplexModel(ArtistResponse artistResponse){
        List<Link> links = new ArrayList<>();

        toModel(artistResponse);

        links.add(linkTo(methodOn(WebController.class).assignSongsToArtist(artistResponse.getId(), null,null)).withRel("assign songs").withType("POST"));
        links.add(linkTo(methodOn(WebController.class).deleteArtist(artistResponse.getId(),null)).withRel("delete artist").withType("DELETE"));

        artistResponse.add(links);

    }

    @Override
    public CollectionModel<ArtistResponse> toCollectionModel(Iterable<? extends ArtistResponse> artistDTOS) {
        CollectionModel<ArtistResponse> newArtistDTOS = super.toCollectionModel(artistDTOS);
        newArtistDTOS.add(linkTo(methodOn(WebController.class).getAllArtists(null, null, null, null)).withSelfRel());

        return newArtistDTOS;
    }
}
