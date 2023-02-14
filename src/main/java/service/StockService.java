package service;

import entity.Stock;
import repository.StockRepository;

import java.util.ArrayList;
import java.util.List;

public class StockService implements StockRepository {

    private final List<Stock> stocks = new ArrayList<>();

    @Override
    public List<Stock> findAll() {
        return stocks;
    }

    @Override
    public void save(Stock stock) {
        stocks.add(stock);
    }
}
