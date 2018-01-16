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

    /**
     * Returns one of the following responses:
     * <ul>
     * <li>OK - HTTP Code 200: body = dtoList
     * <li>NOT FOUND - HTTP Code 404: body = null
     * <li>NO CONTENT - HTTP Code 204: body = null due to empty dtoList
     * </ul>
     * 
     * @return dtoList response
     */
    Response get();
    /**
     * Returns one of the following responses:
     * <ul>
     * <li>OK - HTTP Code 200: body = dto
     * <li>NOT FOUND - HTTP Code 404: body = null
     * </ul>
     * 
     * @param id
     * @return dto response
     */
    Response get(String id);
    /**
     * Returns one of the following responses:
     * <ul>
     * <li>OK - HTTP Code 200: body = dto
     * <li>NOT FOUND - HTTP Code 404: body = null
     * </ul>
     * 
     * @param id
     * @return dto response
     */
    Response get(DTO id);

    /**
     * Returns one of the following responses:
     * <ul>
     * <li>NOT FOUND - HTTP Code 404: body = null
     * <li>NO CONTENT - HTTP Code 204: body = null
     * <li>INTERNAL SERVER ERROR - HTTP Code 500
     * </ul>
     * 
     * @return response
     */
    Response delete(String id);
    /**
     * Returns one of the following responses:
     * <ul>
     * <li>NOT FOUND - HTTP Code 404: body = null
     * <li>NO CONTENT - HTTP Code 204: body = null
     * <li>INTERNAL SERVER ERROR - HTTP Code 500
     * </ul>
     * 
     * @return response
     */
    Response delete(DTO id);

    /**
     * Returns one of the following responses:
     * <ul>
     * <li>NOT FOUND - HTTP Code 404: body = null
     * <li>NO CONTENT - HTTP Code 204: body = null
     * <li>INTERNAL SERVER ERROR - HTTP Code 500
     * </ul>
     * 
     * @return response
     */
    Response put(String id, DTO dto);
    /**
     * Returns one of the following responses:
     * <ul>
     * <li>NOT FOUND - HTTP Code 404: body = null
     * <li>NO CONTENT - HTTP Code 204: body = null
     * <li>INTERNAL SERVER ERROR - HTTP Code 500
     * </ul>
     * 
     * @return response
     */
    Response put(DTO id, DTO dto);

    /**
     * Returns one of the following responses:
     * <ul>
     * <li>CREATED - HTTP Code 201: URL location to access the created DTO
     * <li>INTERNAL SERVER ERROR - HTTP Code 500
     * </ul>
     * 
     * @param dto
     * @return response
     */
    Response post(DTO dto);
}
