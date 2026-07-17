package supernova.pagination;

import supernova.annotations.Concept;

import java.util.Collection;

/**
 * A collection of elements divided into fixed-size pages.
 *
 * <p>A pagination provides sequential access to its elements while exposing
 * them through logical pages. Each page contains up to a configurable number
 * of elements.
 *
 * <p>The iteration order is the natural order of the elements across all pages,
 * beginning with the first page and ending with the last.
 *
 * @param <E> the type of elements contained in this pagination
 *
 * @author Izhar Atharzi
 * @since 1.1.1
 */
@Concept
public interface Pagination<E> extends Iterable<E> {

    /**
     * Gets the maximum number of elements each page can contain.
     *
     * @return the page size
     */
    int pageSize();

    /**
     * Gets the total number of the page.
     *
     * @return the total number of the page
     */
    int pageCount();

    /**
     * Checks if the pagination is empty.
     *
     * @return {@code true} if there was no page available
     */
    boolean isEmpty();

    /**
     * Add an element to the pagination.
     *
     * <p>It will search available page or creating new one and insert the element into the page.</p>
     *
     * @param element the instance of the element
     * @return {@code true} as specified by {@link Page#add(Object)}
     */
    boolean add(E element);

    /**
     * Remove an element from the pagination.
     *
     * @param element the instance of the element
     * @return {@code true} as specified by {@link Page#remove(Object)}
     */
    boolean remove(E element);

    /**
     * Remove an element at the specified index.
     *
     * @param index the index of the element
     * @return {@code true} if removed
     */
    boolean remove(int index);

    /**
     * Returns the page with the specified page number.
     *
     * <p>Page numbers are one-based. The first page has a page number of
     * {@code 1}.
     *
     * @param pageNumber the page number
     * @return the page with the specified page number
     * @throws IndexOutOfBoundsException if the page number is less than
     *         {@code 1} or greater than {@code pageCount()}
     */
    Page<E> get(int pageNumber);

    /**
     * Returns the page containing the element at the specified index.
     *
     * @param index the index of the element
     * @return the page containing the specified element
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size()})
     */
    Page<E> getFromIndex(int index);

    /**
     * Gets the first page of the pagination.
     *
     * @return the first page
     */
    default Page<E> getFirst() {
        return get(1);
    }

    /**
     * Gets the last page of the pagination.
     *
     * @return the last page
     */
    default Page<E> getLast() {
        return get(pageCount());
    }

    /**
     * Gets a collection of pages.
     *
     * @return a collection of pages.
     */
    Collection<Page<E>> pages();
}