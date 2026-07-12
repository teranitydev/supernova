package supernova.pagination;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Experimental
public class PaginationList<E> implements Pagination<E> {

    /**
     * The default number of elements contained in each page.
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * The maximum number of elements each page can contain.
     */
    private final int pageSize;

    /**
     * The pages that make up this pagination.
     */
    private final List<Page<E>> pages;

    /**
     * The size of the page that pagination have.
     */
    private int size;

    /**
     * Creates a new pagination with the specified page size.
     *
     * <p>If {@code pageSize} is {@code 0}, {@link #DEFAULT_PAGE_SIZE}
     * will be used instead.
     *
     * @param pageSize the maximum number of elements per page
     */
    public PaginationList(int pageSize) {
        if (pageSize == 0) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }

        this.size = 0;
        this.pages = new ArrayList<>();
    }

    /**
     * Creates a new pagination with no param.
     */
    public PaginationList() {
        this(0);
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E element) {
        Page<E> page = getOrAddPage();

        if (page.values().size() >= pageSize) {
            page = addPage();
        }

        return page.add(element);
    }

    /**
     * Get existing page or add new page.
     */
    private Page<E> getOrAddPage() {
        if (pages.isEmpty()) {
            return addPage();
        }

        return pages.getLast();
    }

    /**
     * Add new page.
     */
    private Page<E> addPage() {
        Page<E> page = new PageNode<>(
                this,
                new ArrayList<>(),
                size++
        );
        pages.add(page);
        return page;
    }

    @Override
    public boolean remove(E element) {
        List<E> elements = toList();

        if (!elements.remove(element)) {
            return false;
        }

        rebuildPages(elements);
        return true;
    }

    private void rebuildPages(List<E> elements) {
        pages.clear();

        for (E element : elements) {
            add(element);
        }
    }

    @Override
    public Page<E> get(int page) {
        return pages.get(page);
    }

    @Override
    public Page<E> getFirst() {
        return pages.getFirst();
    }

    @Override
    public @NotNull Page<E> getLast() {
        return pages.getLast();
    }

    /**
     * @return Unmodified list of pages
     */
    @Override
    public List<Page<E>> pages() {
        return Collections.unmodifiableList(pages);
    }

    private List<E> toList() {
        List<E> elements = new ArrayList<>();

        for (Page<E> page : pages) {
            elements.addAll(page.values());
        }

        return elements;
    }

    /**
     * Implementation of the page of pagination.
     */
    public static class PageNode<E> implements Page<E> {

        private final PaginationList<E> parent;

        private final List<E> list;
        private final int pageNumber;

        public PageNode(PaginationList<E> parent, List<E> list, int pageNumber) {
            this.parent = parent;
            this.list = list;
            this.pageNumber = pageNumber;
        }

        @Override
        public int pageNumber() {
            return pageNumber;
        }

        @Override
        public boolean add(E element) {
            return list.add(element);
        }

        @Override
        public List<E> values() {
            return Collections.unmodifiableList(list);
        }

        @Override
        public Page<E> nextPage() {
            return parent.get(pageNumber + 1);
        }

        @Override
        public Page<E> previousPage() {
            return parent.get(pageNumber - 1);
        }
    }
}
