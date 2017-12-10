package co.dewald.guardian.dao;


import java.util.List;


/**
 * @author Dewald Pretorius
 */
public interface DAO<DO, ID> {
    
    List<DO> fetch();
    
    DO find(ID id);
    
    void delete(ID id);

    void update(ID id, DO dataObject);

    void create(DO dataObject);
}
