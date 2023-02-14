package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class StockData {
    private final Stock stock;
    private final BigDecimal lastDividend;
    private final BigDecimal parValue;
    private BigDecimal fixedDividend;
}
