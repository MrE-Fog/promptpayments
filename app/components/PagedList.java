package components;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * List with some paging metadata
 */
public class PagedList<T> extends ArrayList<T> {
    private int totalSize;
    private int pageNumber;
    private int pageSize;
    public PagedList(Collection<? extends T> c, int totalSize, int pageNumber, int pageSize) {
        super(c);
        this.totalSize = totalSize;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int totalSize() {
        return totalSize;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int rangeLower() {
        return Math.min(pageNumber*pageSize, totalSize);
    }

    public int rangeUpper() { return Math.max(Math.min(totalSize - 1, (pageNumber + 1) * pageSize - 1), rangeLower()); }

    public boolean canGoBack() {
        return rangeLower() > 0 && rangeLower() < totalSize;
    }

    public boolean canGoNext() {
        return rangeUpper() < totalSize - 1;
    }
}
