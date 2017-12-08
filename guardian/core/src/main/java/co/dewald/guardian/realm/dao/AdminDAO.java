package co.dewald.guardian.realm.dao;


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import co.dewald.guardian.realm.RealmEntity;


/**
 * @param <Model>
 * @param <DTO>
 * 
 * @author Dewald Pretorius
 */
public interface AdminDAO<Model extends RealmEntity, DTO> {
    
    Function<Model, DTO> model2dto();

    default List<DTO> fetch(TypedQuery<Model> query) {
        List<Model> modelList = query.getResultList();
        List<DTO> dtoList = modelList.stream().map(model2dto()).collect(Collectors.toList());
        
        return dtoList;
    }
}
