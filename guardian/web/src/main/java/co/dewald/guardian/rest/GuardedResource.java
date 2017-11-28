package co.dewald.guardian.rest;


import co.dewald.guardian.gate.Guard;


/**
 * @author Dewald Pretorius
 */
@Guard
public interface GuardedResource<T> {
    
    <UK> T find(UK uniqueKey);

    void create(T resource);
    
    void update(T resource);
    
    void delete(T resource);
}
