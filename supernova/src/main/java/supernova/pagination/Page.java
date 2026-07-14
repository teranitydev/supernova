package supernova.pagination;

import supernova.annotations.Concept;

import java.util.List;

/**
 * Represents a single page.
 *
 * @param <E> the type of elements contained in the page
 * @author Izhar Atharzi
 * @since 1.0.0
 */
@Concept
public interface Page<E> {

    /**
     * Gets the page number.
     *
     * @return the page number
     */
    int pageNumber();

    /**
     * Add element into the page list.
     *
     * @param element The element
     * @return {@code true} if the operation is success
     */
    boolean add(E element);

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