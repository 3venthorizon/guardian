package co.dewald.guardian.service.rest;


import javax.ws.rs.core.Response;

import co.dewald.guardian.dto.DTO;


/**
 * @param <DTO> 
 * 
 * @author Dewald Pretorius
 */
@SuppressWarnings("hiding")
public interface Resource<DTO extends co.dewald.guardian.dto.DTO> {

    Response fetch();

    Response find(String id);
    Response find(DTO id);

    Response delete(String id);
    Response delete(DTO id);

    Response update(String id, DTO dto);
    Response update(DTO id, DTO dto);

    Response create(DTO dto);
}
