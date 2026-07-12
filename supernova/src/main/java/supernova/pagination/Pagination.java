package supernova.pagination;

import java.util.List;

/**
 * Represents a collection of elements divided into pages.
 *
 * @param <E> the type of elements contained in this pagination
 * @author Izhar Atharzi
 * @since 1.0.0
 */
public interface Pagination<E> {

    /**
     * Returns the maximum number of elements each page can contain.
     *
     * @return the page size
     */
    int pageSize();

    /**
     * Returns the size of the page that pagination have.
     *
     * @return the size
     */
    int size();

    /**
     * Add element into pagination.
     *
     * @param element the object
     * @return {@code true} if the operation is success
     */
    boolean add(E element);

    /**
     * Remove element from pagination.
     *
     * @param element The object
     * @return {@code true} if the operation is success
     */
    boolean remove(E element);

    /**
     * Gets a page from the page number.
     *
     * @param page the page number
     * @return the page
     */
    Page<E> get(int page);

    /**
     * Returns the first page of the pagination.
     *
     * @return the first page of the pagination.
     */
    Page<E> getFirst();

    /**
     * Returns the last page of the pagination.
     *
     * @return the last page of the pagination
     */
    Page<E> getLast();

    /**
     * Returns all pages in this pagination.
     *
     * @return list of pages.
     */
    List<Page<E>> pages();
}