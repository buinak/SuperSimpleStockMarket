package repository;

import entity.Stock;
import entity.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TradeRepository {
    List<Trade> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    void recordTrade(Trade trade);
    Optional<BigDecimal> getLastTradedPrice(Stock stock);
}
