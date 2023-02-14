package repository;

import entity.Stock;

import java.util.List;

// Extend JPA repository or similar, depending on libraries/framework used, in a real application, for now mock in tests.
public interface StockRepository {
    List<Stock> findAll();
    void save(Stock stock);
}
