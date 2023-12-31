package com.company.app.wildberries_searcher.component.impl;

import com.company.app.core.aop.logging.performance.PerformanceLogAnnotation;
import com.company.app.core.aop.logging.util.LogUtils;
import com.company.app.wildberries_desire_lot.component.common.data.ResponseProducts;
import com.company.app.wildberries_searcher.component.api.WildberriesSearcherAveragePriceExtractor;
import com.company.app.wildberries_searcher.component.api.WildberriesSearcherFilterer;
import com.company.app.wildberries_searcher.component.api.WildberriesSearcherNotificator;
import com.company.app.wildberries_searcher.component.data.WildberriesSearcherContainer;
import com.company.app.wildberries_searcher.component.data.filter.WildberriesSearcherFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WildberriesSearcherFiltererImpl implements WildberriesSearcherFilterer {

    /**
     * Нужно для логов. Будет только один поток, гарантирую.
     */
    private int preparedProductsSize;
    private int filterPosition;

    @Autowired
    private WildberriesSearcherAveragePriceExtractor wildberriesSearcherAveragePriceExtractor;
    @Autowired
    private WildberriesSearcherNotificator wildberriesSearcherNotificator;
    @Autowired
    private List<WildberriesSearcherFilter> wildberriesSearcherFilterList;

    @PerformanceLogAnnotation
    @Override
    public List<ResponseProducts> filter(List<ResponseProducts> products, WildberriesSearcherContainer wildberriesSearcherContainer) {
        List<ResponseProducts> preparedProducts = products.stream()
                .filter(responseProducts -> filterAll(responseProducts, wildberriesSearcherContainer))
                .collect(Collectors.toList());

        if (log.isDebugEnabled()) {
            log.debug("[{}]: После предварительной фильтрации осталось [{}] шт.", wildberriesSearcherContainer.getChatName(), preparedProducts.size());
            preparedProductsSize = preparedProducts.size();
            filterPosition = 1;
        }

        return preparedProducts.stream()
                .filter(responseProducts -> currentPriceLesserThanAveragePrice(responseProducts, wildberriesSearcherContainer))
                .map(responseProducts -> notify(responseProducts, wildberriesSearcherContainer))
                .collect(Collectors.toList());
    }

    private ResponseProducts notify(ResponseProducts responseProducts, WildberriesSearcherContainer wildberriesSearcherContainer) {
        wildberriesSearcherNotificator.notify(responseProducts, wildberriesSearcherContainer);
        return responseProducts;
    }

    private boolean filterAll(ResponseProducts responseProducts, WildberriesSearcherContainer wildberriesSearcherContainer) {
        for (WildberriesSearcherFilter filter : wildberriesSearcherFilterList) {
            if (filter.isPreFilter()) {
                boolean result = filter.doFilter(responseProducts, wildberriesSearcherContainer);
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean currentPriceLesserThanAveragePrice(ResponseProducts responseProducts, WildberriesSearcherContainer wildberriesSearcherContainer) {
        try {
            BigDecimal averagePrice = new BigDecimal(wildberriesSearcherAveragePriceExtractor.getAveragePrice(responseProducts));
            BigDecimal currentPrice = new BigDecimal(responseProducts.getSalePriceU());
            currentPrice = currentPrice.multiply(new BigDecimal(wildberriesSearcherContainer.getGreedIndex()));
            doLog(wildberriesSearcherContainer, responseProducts, averagePrice, currentPrice);
            int i = currentPrice.compareTo(averagePrice);
            return i < 0;
        } catch (Exception exception) {
            LogUtils.doExceptionLog(exception, String.format("[%s] Проблема с [%s]:", wildberriesSearcherContainer.getChatName(), responseProducts.getId()));
            return false;
        }
    }

    private void doLog(WildberriesSearcherContainer wildberriesSearcherContainer, ResponseProducts responseProducts, BigDecimal averagePrice, BigDecimal currentPrice) {
        if (log.isDebugEnabled()) {
            log.debug("[{}]: [{}]: Цена: текущая*[{}]: [{}] < средняя: [{}] ? [{}/{}]",
                    wildberriesSearcherContainer.getChatName(),
                    responseProducts.getId(),
                    wildberriesSearcherContainer.getGreedIndex(),
                    currentPrice,
                    averagePrice,
                    filterPosition,
                    preparedProductsSize
            );
            filterPosition++;
        }
    }
}
