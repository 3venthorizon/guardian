package co.dewald.guardian.service.rest;


import java.net.URI;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.dao.DAO;


/**
 * 
 * @author Dewald Pretorius
 */
public abstract class BaseResource<DTO extends co.dewald.guardian.dto.DTO> implements Resource<DTO> {
    
    public static final String RESOURCE_ID = "Resource ID: ";
    
    protected abstract DAO<DTO> getDAO();
    protected abstract ResourceContext getResourceContext(); 
    protected abstract UriInfo getUriInfo();
    
    @Override
    public Response fetch() {
        List<DTO> dtoList = getDAO().fetch();
        if (dtoList.isEmpty()) return Response.noContent().build();

        GenericEntity<List<DTO>> genericDTOList = new GenericEntity<List<DTO>>(dtoList) {};
        return Response.ok(genericDTOList).build();
    }
    
    @Override
    public Response find(String id) {
        DTO dto = getDAO().find(id);
        if (dto == null) throw new NotFoundException(RESOURCE_ID + id);
        
        return Response.ok(dto).build();
    }
    
    @Override
    public Response delete(String id) {
        Boolean success = getDAO().delete(id);
        if (success == null) throw new NotFoundException(RESOURCE_ID + id);
        if (Boolean.FALSE.equals(success)) throw new InternalServerErrorException("Unable to Delete Resource ID:" + id);
        
        return Response.noContent().build();
    }
    
    @Override
    public Response update(String id, DTO dto) {
        Boolean success = getDAO().update(id, dto);
        if (success == null) throw new NotFoundException(RESOURCE_ID + id);
        if (Boolean.FALSE.equals(success)) throw new InternalServerErrorException("Unable to Update Resource ID:" + id);
        
        return Response.noContent().build();
    }
    
    @Override
    public Response create(DTO dto) {
        String id = getDAO().create(dto);
        if (id == null) throw new InternalServerErrorException("Unable to create Resource");
        
        URI location = getUriInfo().getAbsolutePathBuilder().path(id).build();
        return Response.created(location).build();
    }
}
