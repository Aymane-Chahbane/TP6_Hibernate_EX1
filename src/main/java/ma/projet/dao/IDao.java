package ma.projet.dao;

import java.util.List;

/**
 * Interface générique CRUD.
 * @param <T>  type de l'entité
 * @param <ID> type de la clé primaire
 */
public interface IDao<T, ID> {
    boolean create(T o);
    boolean delete(T o);
    boolean update(T o);
    T       findById(ID id);
    List<T> findAll();
}
