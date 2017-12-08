package co.dewald.guardian.admin;


import java.util.List;

import co.dewald.guardian.gate.Grant;


/**
 * @author Dewald Pretorius
 */
public interface AdminResource<R, ID> {
    
    List<R> fetch();
    
    R find(ID id);
    
    void delete(ID id);

    void update(ID id, R resource);

    void create(R resource);

    @Grant(check = false)
    ID getId(R resource);

    @Grant(check = false)
    default R load(R resource) {
        ID id = getId(resource);
        R loaded = find(id);
        
        return loaded;
    }
}
