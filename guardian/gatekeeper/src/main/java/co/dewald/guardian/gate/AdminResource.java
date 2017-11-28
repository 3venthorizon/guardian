package co.dewald.guardian.gate;


/**
 * @author Dewald Pretorius
 */
public interface AdminResource<R, UK> {
    
    @Grant(filter = true)
    R find(@Grant(name = "uniqueKey", filter = true) UK uniqueKey);

    void create(R resource);
    
    void update(R resource);
    
    void delete(R resource);
}
