package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Trade {

    private final Stock stock;
    private final TradeSide tradeSide;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private LocalDateTime time = LocalDateTime.now();

    public BigDecimal getValue(){
        return price.multiply(quantity);
    }

}
