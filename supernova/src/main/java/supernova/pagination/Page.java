package supernova.pagination;

import supernova.annotations.Concept;

import java.util.List;

/**
 * A {@link List} representing a single page within a {@link Pagination}.
 *
 * <p>A page contains a contiguous subset of elements from its owning
 * pagination. The number of elements contained in a page is less than or
 * equal to the pagination's configured page size.
 *
 * @param <E> the type of elements contained in this page
 *
 * @author Izhar Atharzi
 * @since 1.1.1
 */
@Concept
public interface Page<E> extends List<E> {

    /**
     * Gets the current number of page that this page is currently on.
     *
     * @return the current number of the page
     */
    int pageNumber();

    /**
     * Gets the parent of the page.
     *
     * @return a pagination
     */
    Pagination<E> pagination();

    /**
     * Gets the next page.
     *
     * @return the next page
     */
    Page<E> nextPage();

    /**
     * Gets the previous page.
     *
     * @return the previous page
     */
    Page<E> previousPage();

    /**
     * Returns whether this page has a next page.
     *
     * @return {@code true} if a next page exists; {@code false} otherwise
     */
    default boolean hasNext() {
        return pageNumber() + 1 < pagination().pageCount();
    }

    /**
     * Returns whether this page has a previous page.
     *
     * @return {@code true} if a previous page exists; {@code false} otherwise
     */
    default boolean hasPrevious() {
        return pageNumber() > 0;
    }
}