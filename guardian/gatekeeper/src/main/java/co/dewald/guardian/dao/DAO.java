package co.dewald.guardian.dao;


import java.util.List;

import co.dewald.guardian.dto.DTO;


/**
 * Interface for Data Access Objects.
 * 
 * @param <DO> Data Object
 * @param <ID> ID identifier for Data Object
 * 
 * @author Dewald Pretorius
 */
public interface DAO<DO extends DTO> {
    
    /**
     * Fetches Data Objects.
     * 
     * @return list of Data Objects
     */
    List<DO> fetch();
    
    /**
     * Finds a Data Object by its ID.
     * 
     * @param id 
     * @return Data Object or null when not found.
     */
    DO find(String id);
    
    default DO find(DO id) {
        return find(id.getId());
    }
    
    /**
     * Deletes the underlying Data Object by ID.
     * 
     * @param id
     * @return success or null when the Data Object was not found.
     */
    Boolean delete(String id);
    
    default Boolean delete(DO id) {
        return delete(id.getId());
    }

    /**
     * Updates the Data Object with the following steps:
     * <ol>
     * <li>Find the underlying Data Object by its ID.
     * <li>Updates the above object with the parameter Data Object.
     * </ol>
     * 
     * @param id
     * @param dataObject
     * @return success or null when the Data Object was not found.
     */
    Boolean update(String id, DO dataObject);
    
    default Boolean update(DO id, DO dataObject) {
        return update(id.getId(), dataObject);
    }

    /**
     * Creates a new Data Object.
     * 
     * @param dataObject
     * @return id for the created Data Object or null when the creation failed.
     */
    String create(DO dataObject);
}
