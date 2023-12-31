package com.company.app.wildberries_searcher.component.api;

import com.company.app.wildberries_desire_lot.component.common.data.ResponseProducts;
import com.company.app.wildberries_searcher.component.data.WildberriesSearcherContainer;

public interface WildberriesSearcherNotificator {

    void notify(ResponseProducts responseProducts, WildberriesSearcherContainer wildberriesSearcherContainer);
}
