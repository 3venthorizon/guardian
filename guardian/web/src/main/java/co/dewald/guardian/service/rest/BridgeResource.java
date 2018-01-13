package co.dewald.guardian.service.rest;


import java.net.URI;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.dao.DAO;


/**
 * 
 * @author Dewald Pretorius
 */
public abstract class BridgeResource<DTO extends co.dewald.guardian.dto.DTO> implements Resource<DTO> {
    
    protected Response supResponse;
    
    protected abstract DAO<DTO> getDAO();
    protected abstract UriInfo getUriInfo();
    
    @Override
    public Response get() {
        List<DTO> dtoList = getDAO().fetch();
        return content(dtoList);
    }
    
    @Override
    public Response get(String id) {
        DTO dto = getDAO().find(id);
        return content(dto);
    }
    
    @Override
    public Response get(DTO id) {
        DTO dto = getDAO().find(id);
        return content(dto);
    }
    
    @Override
    public Response delete(String id) {
        Boolean success = getDAO().delete(id);
        return noContent(success);
    }
    
    @Override
    public Response delete(DTO id) {
        Boolean success = getDAO().delete(id);
        return noContent(success);
    }
    
    @Override
    public Response put(String id, DTO dto) {
        Boolean success = getDAO().update(id, dto);
        return noContent(success);
    }
    
    @Override
    public Response put(DTO id, DTO dto) {
        Boolean success = getDAO().update(id, dto);
        return noContent(success);
    }
    
    @Override
    public Response post(DTO dto) {
        String id = getDAO().create(dto);
        return created(id);
    }
    
    /**
     * Gets the Super Resource's response.
     * 
     * @return supResponse of supper resource or null if this resource was not a delegated
     */
    public Response getSupResponse() {
        return supResponse;
    }
    
    /**
     * Sets the Super Resource's response.
     * 
     * @param supResponse
     */
    public void setSupResponse(Response supResponse) {
        this.supResponse = supResponse;
    }
    
    protected Response content(DTO dto) {
        if (dto == null) throw new NotFoundException();
        return Response.ok(dto).build();
    }
    
    protected Response content(List<DTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) return Response.noContent().build();

        GenericEntity<List<DTO>> genericDTOList = new GenericEntity<List<DTO>>(dtoList) {};
        return Response.ok(genericDTOList).build();
    }
    
    protected Response created(String id) {
        if (id == null) throw new InternalServerErrorException();
        
        URI location = getUriInfo().getAbsolutePathBuilder().path(id).build();
        return Response.created(location).build();
    }
    
    protected Response noContent(Boolean success) {
        if (success == null) throw new NotFoundException();
        if (Boolean.FALSE.equals(success)) throw new InternalServerErrorException();
        
        return Response.noContent().build();
    }
}
