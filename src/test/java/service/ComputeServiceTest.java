package service;

import entity.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import repository.StockDataRepository;
import repository.StockRepository;
import repository.TradeRepository;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class ComputeServiceTest {

    private final StockRepository stockRepository = Mockito.mock(StockRepository.class);
    private final StockDataRepository stockDataRepository = Mockito.mock(StockDataRepository.class);
    private final TradeRepository tradeRepository = Mockito.mock(TradeRepository.class);

    private final ComputeInterface computeInterface =
            new ComputeService(tradeRepository, stockDataRepository, stockRepository);

    @Before
    public void setup() {
        // sample data as per the requirements in the PDF
        List<Stock> stockList = List.of(
                new Stock("TEA", StockType.COMMON),
                new Stock("POP", StockType.COMMON),
                new Stock("ALE", StockType.COMMON),
                new Stock("GIN", StockType.PREFERRED),
                new Stock("JOE", StockType.COMMON));
        Mockito.when(stockRepository.findAll()).thenReturn(stockList);

        Map<Stock, StockData> stockDataMap = new HashMap<>();
        stockDataMap.put(stockList.get(0), new StockData(stockList.get(0), BigDecimal.ZERO, BigDecimal.valueOf(100)));
        stockDataMap.put(stockList.get(1), new StockData(stockList.get(1), BigDecimal.valueOf(8), BigDecimal.valueOf(100)));
        stockDataMap.put(stockList.get(2), new StockData(stockList.get(2), BigDecimal.valueOf(23), BigDecimal.valueOf(60)));
        stockDataMap.put(stockList.get(3), new StockData(stockList.get(3), BigDecimal.valueOf(8), BigDecimal.valueOf(100), BigDecimal.valueOf(2)));
        stockDataMap.put(stockList.get(4), new StockData(stockList.get(4), BigDecimal.valueOf(13), BigDecimal.valueOf(250)));

        Mockito.when(stockDataRepository.findAll()).thenReturn(new ArrayList<>(stockDataMap.values()));
        /*
         configure stock data repository to return correct values for every map entry
         for ticker price simplicity for testing, par value will be returned
        */
        stockDataMap.entrySet().forEach(entry -> {
            Mockito.when(stockDataRepository.findByStock(entry.getKey())).thenReturn(Optional.of(entry.getValue()));
            Mockito.when(stockDataRepository.getTickerPrice(entry.getKey())).thenReturn(entry.getValue().getParValue());
        });

    }

    @Test
    public void calculateVolumeWeightedStockPriceForRecentTrades() {
        List<Trade> sampleTrades = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sampleTrades.add(new Trade(new Stock("TEA", StockType.COMMON), TradeSide.BUY, BigDecimal.valueOf(5), BigDecimal.TEN));
        }

        Mockito.when(tradeRepository.findByDateBetween(any(), any())).thenReturn(sampleTrades);

        BigDecimal volumeWeighted = computeInterface.calculateVolumeWeightedStockPriceForRecentTrades(ChronoUnit.MINUTES, 15);
        // 500 aggregate value (10 trades of price 5 and quantity 10 each) and 100 aggregate quantity (10 trades 10 quantity each) =
        // 5 volume weighted stock price
        assertEquals(volumeWeighted, new BigDecimal("5.0000"));
    }

    @Test
    public void calculateDividendYield() {
        // common div. yield
        Stock popStock = new Stock("POP", StockType.COMMON);
        BigDecimal popDividendYield = computeInterface.calculateDividendYield(popStock, BigDecimal.valueOf(80));
        // last div. = 8, price = 80 => div. yield of 0.1
        assertEquals(popDividendYield, new BigDecimal("0.1000"));

        // preferred div. yield
        Stock ginStock = new Stock("GIN", StockType.PREFERRED);
        BigDecimal ginDividendYield = computeInterface.calculateDividendYield(ginStock, BigDecimal.valueOf(50));
        // fixed div. of 2, par price of 100, price of 50 => (2 * 100) / 50 = 4
        assertEquals(ginDividendYield, new BigDecimal("4.0000"));
    }

    @Test
    public void calculatePERatio() {
        Stock popStock = new Stock("POP", StockType.COMMON);
        BigDecimal popDividendYield = computeInterface.calculatePERatio(popStock, BigDecimal.valueOf(80));
        // last div. = 8, price = 80 => PE ratio of 10
        assertEquals(popDividendYield, new BigDecimal("10.0000"));

        Stock ginStock = new Stock("ALE", StockType.COMMON);
        BigDecimal ginDividendYield = computeInterface.calculatePERatio(ginStock, BigDecimal.valueOf(34.5));
        // last div. = 23, price = 34.5 => PE ratio of 1.5
        assertEquals(ginDividendYield, new BigDecimal("1.5000"));
    }

    @Test
    public void calculateGBCEIndex() {
        /*
        In our test, the ticker price is always equal to par value provided in the sample data (table 1).
        Thus we need to compute geometric mean for [60, 100, 100, 100, 250]
        I used http://www.alcula.com/calculators/statistics/geometric-mean/ to get the number to use in this test --
        which also tests that the scale/rounding is correct :)
         */
        BigDecimal gbceIndex = computeInterface.calculateGBCEIndex();
        assertEquals(gbceIndex, new BigDecimal("108.4472"));
    }
}