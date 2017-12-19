package co.dewald.guardian.dao;


import java.util.List;


/**
 * Interface for Data Access Objects.
 * 
 * @author Dewald Pretorius
 *
 * @param <DO> Data Object
 * @param <ID> ID identifier for Data Object
 */
public interface DAO<DO, ID> {
    
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
    DO find(ID id);
    
    /**
     * Deletes the underlying Data Object by ID.
     * 
     * @param id
     * @return success
     */
    boolean delete(ID id);

    /**
     * Updates the Data Object with the following steps:
     * <ol>
     * <li>Find the underlying Data Object by its ID.
     * <li>Updates the above object with the parameter Data Object.
     * </ol>
     * 
     * @param id
     * @param dataObject
     * @return success
     */
    boolean update(ID id, DO dataObject);

    /**
     * Creates a new Data Object.
     * 
     * @param dataObject
     * @return success
     */
    boolean create(DO dataObject);
}
