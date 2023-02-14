package service;

import entity.Stock;
import entity.Trade;
import repository.TradeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TradeService implements TradeRepository {

    private final Map<Stock, List<Trade>> tradesMap = new HashMap<>();

    @Override
    public List<Trade> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return tradesMap.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(trade -> trade.getTime().isAfter(startDateTime) &&
                        trade.getTime().isBefore(endDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public void recordTrade(Trade trade) {
        tradesMap.getOrDefault(trade.getStock(), new ArrayList<>())
                .add(trade);
    }

    @Override
    public Optional<BigDecimal> getLastTradedPrice(Stock stock) {
        List<Trade> trades = tradesMap.get(stock);
        if (trades == null || trades.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(trades.get(trades.size() - 1).getPrice());
        }

    }
}
