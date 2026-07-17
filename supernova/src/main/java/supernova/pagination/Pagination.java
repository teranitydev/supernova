package supernova.pagination;

import supernova.annotations.Concept;

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
}