package supernova.pagination;

import java.util.List;

/**
 * Represents a single page within a {@link PaginationList}.
 *
 * @param <E> the type of elements contained in the page
 * @author Izhar Atharzi
 * @since 1.0.0
 */
public interface Page<E> {

    /**
     * Gets the page number.
     *
     * @return the page number
     */
    int pageNumber();

    /**
     * Returns the elements contained in this page.
     *
     * @return List of elements
     */
    List<E> values();

    /**
     * Gets the next page from this page.
     *
     * @return The next page
     */
    Page<E> nextPage();

    /**
     * Gets the previous page from this page.
     *
     * @return The previous page
     */
    Page<E> previousPage();
}