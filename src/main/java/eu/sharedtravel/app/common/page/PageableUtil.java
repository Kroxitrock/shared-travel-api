package eu.sharedtravel.app.common.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    private PageableUtil() {
    }

    /**
     * Util method that applies a default sort to a pageable object
     */
    public static Pageable pageableWithDefaultSort(Pageable pageable, Sort sort) {
        if (!pageable.getSort().isEmpty()) {
            sort = pageable.getSort().and(sort);
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
