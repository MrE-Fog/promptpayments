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

    public boolean canGoBack() {
        return canGo(pageNumber-1);
    }

    public boolean canGoNext() {
        return canGo(pageNumber+1);
    }

    public boolean canGo(int pageNumber) {
        if (pageNumber == pageNumber()) {
            return true;
        }
        if (!canPage()) {
            return false;
        }

        return isValidRange(pageNumber);
    }

    private boolean isValidRange(int pageNumber) {
        return (pageNumber) * pageSize < totalSize && pageNumber >= 0;
    }

    public boolean canPage() {
        //check we are in a valid navigational state
        if ((this.pageNumber)*pageSize >= totalSize || this.pageNumber < 0) {
            return false;
        }

        return isValidRange(pageNumber-1) || isValidRange(pageNumber+1);
    }

    public int pageSize() {
        return pageSize;
    }
}
