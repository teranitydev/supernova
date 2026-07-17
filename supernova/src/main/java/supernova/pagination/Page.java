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
 * @since 1.0.0
 */
@Concept
public interface Page<E> extends List<E> {
}