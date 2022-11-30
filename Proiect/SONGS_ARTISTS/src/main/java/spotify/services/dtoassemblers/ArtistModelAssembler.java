package spotify.services.dtoassemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import spotify.controller.WebController;
import spotify.view.responses.ArtistDTO;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ArtistModelAssembler extends RepresentationModelAssemblerSupport<ArtistDTO, ArtistDTO> {
    public ArtistModelAssembler() {
        super(WebController.class, ArtistDTO.class);
    }

    @Override
    public ArtistDTO toModel(ArtistDTO artistDTO) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(WebController.class)
                .getArtistById(artistDTO.getId()))
                .withSelfRel());

        links.add(linkTo(
                methodOn(WebController.class).getAllArtists(null, null, null, null))
                .withRel("parent"));


        links.add(linkTo(methodOn(WebController.class).assignSongsToArtist(artistDTO.getId(), null)).withRel("assign songs").withType("POST"));
        links.add(linkTo(methodOn(WebController.class).deleteArtist(artistDTO.getId())).withRel("delete artist").withType("DELETE"));

        if (artistDTO.getHasSongs()) {
            links.add(linkTo(
                    methodOn(WebController.class).getAllSongsForGivenArtist(artistDTO.getId()))
                    .withRel("songs"));
        }

        artistDTO.add(links);

        return artistDTO;
    }

    @Override
    public CollectionModel<ArtistDTO> toCollectionModel(Iterable<? extends ArtistDTO> artistDTOS) {
        CollectionModel<ArtistDTO> newArtistDTOS = super.toCollectionModel(artistDTOS);
        newArtistDTOS.add(linkTo(methodOn(WebController.class).getAllArtists(null, null, null, null)).withSelfRel());

        return newArtistDTOS;
    }
}
