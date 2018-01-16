package co.dewald.guardian.service.rest;


import java.net.URI;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.dao.DAO;


/**
 * @param <DTO>
 * 
 * @author Dewald Pretorius
 */
public abstract class BridgeResource<DTO extends co.dewald.guardian.dto.DTO> implements Resource<DTO> {
    
    @Context protected ResourceContext resourceContext;
    @Context protected UriInfo uriInfo;
    
    protected abstract DAO<DTO> getDAO();
    
    protected Response response;
    
    @Override
    public Response get() {
        Response response = subGet();
        if (response != null) return response;
        
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
        DTO dtoId = getDAO().getId(id);
        Response response = subLink(false, dtoId);
        if (response != null) return response;
        
        Boolean success = getDAO().delete(id);
        return noContent(success);
    }
    
    @Override
    public Response delete(DTO id) {
        Response response = subLink(false, id);
        if (response != null) return response;
        
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
        Response response = subLink(true, dto);
        if (response != null) return response;
        
        String id = getDAO().create(dto);
        return created(id);
    }
    
    @POST
    public Response post(DTO dto, String... delegatedParameters) {
        return post(dto);
    }
    
    protected <S extends co.dewald.guardian.dto.DTO, R extends BridgeResource<S>> R 
            delegate(String id, Class<R> subResourceType) {
        response = get(id);
        return resourceContext.getResource(subResourceType);
    }
    
    protected <S extends co.dewald.guardian.dto.DTO, R extends BridgeResource<S>> R 
            delegate(DTO id, Class<R> subResourceType) {
        response = get(id);
        return resourceContext.getResource(subResourceType);
    }
    
    protected <S extends co.dewald.guardian.dto.DTO> Response subGet() {
        S superDTO = getSuperResourceDTO();
        if (superDTO == null) return null;
        
        List<DTO> dtoList = getDAO().fetchBy(superDTO);
        return content(dtoList);
    }

    protected <S extends co.dewald.guardian.dto.DTO> Response subLink(boolean link, DTO dtoId) {
        S superDTO = getSuperResourceDTO();
        if (superDTO == null) return null;
        
        Boolean success = getDAO().linkReference(link, dtoId, superDTO);
        
        if (link && Boolean.TRUE.equals(success)) return created(getDAO().getId(dtoId)); //linked
        return noContent(success); //de-linked & errors
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
        
        URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
        return Response.created(location).build();
    }
    
    protected Response noContent(Boolean success) {
        if (success == null) throw new NotFoundException();
        if (Boolean.FALSE.equals(success)) throw new InternalServerErrorException();
        
        return Response.noContent().build();
    }
    
    @SuppressWarnings("unchecked")
    protected <S extends co.dewald.guardian.dto.DTO> S getSuperResourceDTO() {
        List<?> resources = uriInfo.getMatchedResources();
        if (resources.size() <= 1) return null;
        
        Object parentResource = resources.get(1);
        if ((parentResource instanceof BridgeResource) == false) return null;
        
        BridgeResource<S> resource = (BridgeResource<S>) parentResource;
        if (resource.response == null) return null;
        
        return (S) resource.response.getEntity();
    }
}
