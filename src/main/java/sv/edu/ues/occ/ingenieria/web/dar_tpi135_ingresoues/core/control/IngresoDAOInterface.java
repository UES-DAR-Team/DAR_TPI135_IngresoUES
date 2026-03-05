package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import java.util.List;

public interface IngresoDAOInterface <T, ID>{
    void create(T obj)throws IllegalStateException, IllegalArgumentException;
    void delete(T obj)throws IllegalStateException, IllegalArgumentException;
    T update(T obj)throws IllegalStateException, IllegalArgumentException;
    //T findById(Object id)throws IllegalStateException, IllegalArgumentException;
    List<T> findRange(int first, int max)throws IllegalStateException, IllegalArgumentException;
    //int count()throws IllegalStateException;

}
