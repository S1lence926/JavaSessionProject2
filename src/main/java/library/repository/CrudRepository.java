package library.repository;

import java.util.List;
import java.util.Optional;

/**
 * Универсальный CRUD-интерфейс.
 * Все репозитории реализуют этот контракт (полиморфизм).
 *
 * @param <T>  тип сущности
 * @param <ID> тип первичного ключа
 */
public interface CrudRepository<T, ID> {

    /** Сохранить новую сущность. Возвращает сущность с присвоенным id. */
    T save(T entity);

    /** Найти по идентификатору. */
    Optional<T> findById(ID id);

    /** Вернуть все записи. */
    List<T> findAll();

    /** Обновить существующую запись. */
    void update(T entity);

    /** Удалить по id. */
    void deleteById(ID id);

    /** Количество записей. */
    long count();
}
