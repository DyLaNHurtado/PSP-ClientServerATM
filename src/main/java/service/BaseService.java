package service;

import lombok.RequiredArgsConstructor;
import repository.CrudRepository;

import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor // Constructor required with at least the final properties
public abstract class BaseService<T, ID, R extends CrudRepository<T, ID>> {
    protected final R repository;

    //  CRUD Operations


    public List<T> findAll() throws SQLException {
        return repository.findAll();
    }

    // GetById
    public T getById(ID id) throws SQLException {
        return repository.getById(id);
    }

    // SAVE
    public T save(T t) throws SQLException {
        return repository.save(t);
    }

    // UPDATE
    public T update(T t) throws SQLException {
        return repository.update(t);
    }

    // DELETE
    public T delete(T t) throws SQLException {
        return repository.delete(t);
    }
}

