package repository;

import entity.Stock;
import entity.StockData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Extend JPA repository or similar, depending on libraries/framework used, in a real application, for now mock in tests.
public interface StockDataRepository {
    Optional<StockData> findByStock(Stock stock);
    List<StockData> findAll();
    BigDecimal getTickerPrice(Stock stock);
    void save(StockData stockData);
}
