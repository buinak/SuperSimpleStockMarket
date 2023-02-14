package service;

import entity.Stock;

import java.math.BigDecimal;
import java.time.temporal.TemporalUnit;

public interface ComputeInterface {
    BigDecimal calculateVolumeWeightedStockPriceForRecentTrades(TemporalUnit unit, int timeUnitThreshold);
    BigDecimal calculateDividendYield(Stock stock, BigDecimal price);
    BigDecimal calculatePERatio(Stock stock, BigDecimal price);
    BigDecimal calculateGBCEIndex();
}
