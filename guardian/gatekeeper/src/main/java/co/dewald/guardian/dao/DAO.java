package co.dewald.guardian.dao;


import java.util.List;


/**
 * Interface for Data Access for Data Transfer Objects.
 * 
 * @param <DTO> Data Transfer Object
 * 
 * @author Dewald Pretorius
 */
public interface DAO<DTO extends co.dewald.guardian.dto.DTO> {
    
    /**
     * 
     * @param id
     * @return string id from composite dto.
     */
    String getId(DTO id);
    
    DTO getId(String id);

    /**
     * Fetches DTOs.
     * 
     * @return list of DTOs
     */
    List<DTO> fetch();
    
    <C extends co.dewald.guardian.dto.DTO> List<DTO> fetchBy(C criteria);
    
    /**
     * Finds a DTO by its ID.
     * 
     * @param id 
     * @return DTO or null when not found.
     */
    DTO find(String id);
    
    default DTO find(DTO id) {
        return find(getId(id));
    }
    
    /**
     * Deletes the underlying DTO by ID.
     * 
     * @param id
     * @return success or null when the DTO was not found.
     */
    Boolean delete(String id);
    
    default Boolean delete(DTO id) {
        return delete(getId(id));
    }
    

    /**
     * Updates the DTO with the following steps:
     * <ol>
     * <li>Find the underlying DTO by its ID.
     * <li>Updates the above object with the parameter DTO.
     * </ol>
     * 
     * @param id
     * @param dto
     * @return success or null when the DTO was not found.
     */
    Boolean update(String id, DTO dto);
    
    default Boolean update(DTO id, DTO dto) {
        return update(getId(id), dto);
    }

    <R extends co.dewald.guardian.dto.DTO> Boolean linkReference(boolean link, DTO id, R reference);

    /**
     * Creates a new DTO.
     * 
     * @param dto
     * @return id for the created DTO or null when the creation failed.
     */
    String create(DTO dto);
    
}
