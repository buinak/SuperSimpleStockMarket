package service;

import common.ApplicationConstants;
import entity.Stock;
import entity.StockData;
import entity.Trade;
import exception.PERatioUnknownException;
import exception.StockNotFoundException;
import exception.UnsupportedStockTypeException;
import lombok.AllArgsConstructor;
import repository.StockDataRepository;
import repository.StockRepository;
import repository.TradeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;

@AllArgsConstructor
public class ComputeService implements ComputeInterface {

    private final TradeRepository tradeRepository;
    private final StockDataRepository stockDataRepository;
    private final StockRepository stockRepository;

    @Override
    public BigDecimal calculateVolumeWeightedStockPriceForRecentTrades(TemporalUnit unit, int amountOfTimeToSubtract) {
        LocalDateTime now = LocalDateTime.now();
        List<Trade> filteredTrades = tradeRepository
                .findByDateBetween(now.minus(amountOfTimeToSubtract, unit), now);

        BigDecimal aggregateQuantity = BigDecimal.ZERO;
        BigDecimal aggregateValue = BigDecimal.ZERO;

        for (Trade trade : filteredTrades){
            aggregateQuantity = aggregateQuantity.add(trade.getQuantity());
            aggregateValue = aggregateValue.add(trade.getValue());
        }

        return aggregateValue.divide(aggregateQuantity,
                ApplicationConstants.DEFAULT_PRECISION,
                ApplicationConstants.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public BigDecimal calculateDividendYield(Stock stock, BigDecimal price) {
        StockData stockData = getStockDataOrThrow(stock);

        if (price.intValueExact() == 0){
            throw new IllegalArgumentException("Can not calculate dividend yield - price can not be zero!");
        }

        switch (stockData.getStock().getStockType()){
            case COMMON: return stockData.getLastDividend().divide(price,
                    ApplicationConstants.DEFAULT_PRECISION,
                    ApplicationConstants.DEFAULT_ROUNDING_MODE);
            case PREFERRED: return stockData.getFixedDividend()
                    .multiply(stockData.getParValue())
                    .divide(price, ApplicationConstants.DEFAULT_PRECISION, ApplicationConstants.DEFAULT_ROUNDING_MODE);
            default:
                throw new UnsupportedStockTypeException("Stock type " +
                        stockData.getStock().getStockType() + " is unsupported.");
        }
    }

    @Override
    public BigDecimal calculatePERatio(Stock stock, BigDecimal price) {
        StockData stockData = getStockDataOrThrow(stock);

        BigDecimal lastDividend = stockData.getLastDividend();
        if (lastDividend.intValueExact() == 0) {
            throw new PERatioUnknownException();
        }

        return price.divide(lastDividend, ApplicationConstants.DEFAULT_PRECISION, ApplicationConstants.DEFAULT_ROUNDING_MODE);
    }

    @Override
    public BigDecimal calculateGBCEIndex() {
        List<Stock> stocks = stockRepository.findAll();
        /*
         I could not quickly find a way to root to Nth a big decimal, so I'm converting it to a double as a work-around.
         In a real application this should be avoided, however also in this case we're not losing any precision in this conversion
         , so maybe it would even be OK.
        */
        double aggregatePrice = stocks.stream()
                // aggregate all stocks' prices
                .map(stockDataRepository::getTickerPrice)
                .reduce(BigDecimal.ONE, BigDecimal::multiply)
                // to the negative power
                .doubleValue();

        return BigDecimal.valueOf(Math.pow(aggregatePrice, (double) 1 / stocks.size()))
                .setScale(ApplicationConstants.DEFAULT_PRECISION, ApplicationConstants.DEFAULT_ROUNDING_MODE);
    }

    private StockData getStockDataOrThrow(Stock stock) {
        return stockDataRepository.findByStock(stock)
                .orElseThrow(StockNotFoundException::new);
    }
}
