/*
The MIT License (MIT)

Copyright (c) 2015 Los Andes University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package co.edu.uniandes.csw.artwork.resources;

import co.edu.uniandes.csw.artwork.api.IArtistLogic;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import co.edu.uniandes.csw.artwork.api.IArtworkLogic;
import co.edu.uniandes.csw.artwork.dtos.detail.ArtworkDetailDTO;
import co.edu.uniandes.csw.artwork.entities.ArtistEntity;
import co.edu.uniandes.csw.artwork.entities.ArtworkEntity;
import java.util.ArrayList;
import java.util.Calendar;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;

/**
 * @generated
 */
@Path("/artworks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RootArtworkResource {

    @Inject private IArtworkLogic artworkLogic;
    @Inject private IArtistLogic artistLogic;
    @Context private HttpServletResponse response;
    @QueryParam("page") private Integer page;
    @QueryParam("limit") private Integer maxRecords;
    @QueryParam("artist") private String artist;
    @PathParam("artistsId") private Long artistId;
    private static final String X_COUNT="X-Total-Count";
   
    /**
     * Convierte una lista de ArtworkEntity a una lista de ArtworkBasicDTO
     *
     * @param entityList Lista de ArtworkEntity a convertir
     * @return Lista de ArtworkBasicDTO convertida
     * @generated
     */
    private List<ArtworkDetailDTO> listEntity2DTO(List<ArtworkEntity> entityList){
        List<ArtworkDetailDTO> list = new ArrayList<>();
        for (ArtworkEntity entity : entityList) {
            list.add(new ArtworkDetailDTO(entity));
        }
        return list;
    }


    /**
     * Obtiene la lista de los registros de Artist
     *
     * @return Colección de objetos de ArtworkDetailDTO
     * @generated
     */
    @GET
    public List<ArtworkDetailDTO> getArtworks() {
        if (page != null && maxRecords != null) {
            this.response.setIntHeader(X_COUNT, artworkLogic.countArtworks());
            return listEntity2DTO(artworkLogic.getArtworks(page, maxRecords,null));
        }
        else if(artist != null && artist.length() > 0){
            this.response.setIntHeader(X_COUNT, artworkLogic.countArtworks());
            return listEntity2DTO(artworkLogic.getArtworksFromArtist(artist));
        }
        return listEntity2DTO(artworkLogic.getArtworks(null,null,null));
    }
    
    /**
     * Obtiene la lista de las ultimas obras registradas
     *
     * @return Colección de objetos de ArtworkDetailDTO
     * @generated
     */
    @GET
    @Path("latest")
    public List<ArtworkDetailDTO> getLatestArtworks(){
        return listEntity2DTO(artworkLogic.getLatestArtworks());
    }
    
    /**
     * Obtiene la lista de los registros de Artwork por categoria.
     *
     * @param categoryid id de la categoria.
     * @return Colección de objetos de ArtworkDetailDTO.
     * @generated
     */
    @GET
    @Path("{categoryid: \\d+}")
    public List<ArtworkDetailDTO> getArtworkByCategory(@PathParam("categoryid") Long categoryid) {
        if (page != null && maxRecords != null) {
            this.response.setIntHeader(X_COUNT, artworkLogic.countArtworks());
            return listEntity2DTO(artworkLogic.getArtworkByCategory(page, maxRecords,categoryid));
        }
        return listEntity2DTO(artworkLogic.getArtworkByCategory(null,null,categoryid));
    }
    
    /**
     * Obtiene el artwork con id dado.
     *
     * @param artworkId id del artwork.
     * @return instancia ArtworkDetailDTO.
     * @generated
     */
    @GET
    @Path("artwork/{artworkId: \\d+}")
    public ArtworkDetailDTO getArtwork(@PathParam("artworkId") Long artworkId) {
        ArtworkEntity entity = artworkLogic.getArtwork(artworkId);
        return new ArtworkDetailDTO(entity);
    }
    public void existsArtist(Long id){
        ArtistEntity artista = artistLogic.getArtist(id);
        if (artista == null) {
            throw new WebApplicationException(404);
        }
    }
    
    @POST
    public ArtworkDetailDTO createArtwork(ArtworkDetailDTO dto){
        existsArtist(dto.getArtist().getId());
        dto.setDateAdded(Calendar.getInstance().getTime());
        return new ArtworkDetailDTO(artworkLogic.createArtwork(dto.getArtist().getId(), dto.toEntity()));
    }
    
    /**
     * Elimina una instancia de Artwork de la base de datos.
     *
     * @param artworkId Identificador de la instancia a eliminar.
     * @generated
     */
    @DELETE
    @Path("{artworkId: \\d+}")
    public void deleteArtwork(@PathParam("artworkId") Long artworkId) {
        artworkLogic.deleteArtwork(artworkId);
    }
}
