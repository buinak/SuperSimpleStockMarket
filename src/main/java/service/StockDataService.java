package service;

import entity.Stock;
import entity.StockData;
import lombok.AllArgsConstructor;
import repository.StockDataRepository;
import repository.TradeRepository;

import java.math.BigDecimal;
import java.util.*;

@AllArgsConstructor
public class StockDataService implements StockDataRepository {

    private final Map<Stock, StockData> stocks
            = new HashMap<>();

    private final TradeRepository tradeRepository;

    @Override
    public Optional<StockData> findByStock(Stock stock) {
        return Optional.ofNullable(stocks.get(stock));
    }

    @Override
    public List<StockData> findAll() {
        return new ArrayList<>(stocks.values());
    }

    @Override
    public BigDecimal getTickerPrice(Stock stock) {
        return tradeRepository.getLastTradedPrice(stock)
                .orElse(stocks.get(stock).getParValue());
    }

    @Override
    public void save(StockData stockData) {
        stocks.put(stockData.getStock(), stockData);
    }
}
