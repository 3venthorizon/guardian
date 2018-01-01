package co.dewald.guardian.service.rest;


import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.dao.DAO;


/**
 * @author Dewald Pretorius
 */
public abstract class Resource<DTO extends co.dewald.guardian.dto.DTO> {
    
    public static final String RESOURCE_404 = "Resource ID: ";
    
    protected DAO<DTO> dao;
    
    protected abstract void initDAO();
    
    public Response find(String id) {
        DTO dto = dao.find(id);
        if (dto == null) throw new NotFoundException(RESOURCE_404 + id);
        
        return Response.ok(dto).build();
    }
    
    public Response delete(String id) {
        Boolean success = dao.delete(id);
        if (success == null) throw new NotFoundException(RESOURCE_404 + id);
        if (Boolean.FALSE.equals(success)) throw new InternalServerErrorException("Unable to Delete Resource ID:" + id);
        
        return Response.noContent().build();
    }
    
    public Response update(String id, DTO dto) {
        Boolean success = dao.update(id, dto);
        if (success == null) throw new NotFoundException(RESOURCE_404 + id);
        if (Boolean.FALSE.equals(success)) throw new InternalServerErrorException("Unable to Update Resource ID:" + id);
        
        return Response.noContent().build();
    }
    
    public Response create(DTO dto, @Context UriInfo uriInfo) {
        String id = dao.create(dto);
        if (id == null) throw new InternalServerErrorException("Unable to create Resource");
        
        //TODO return uri path 
        return Response.ok().build();
    }
}
