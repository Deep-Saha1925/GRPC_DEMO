package com.deep.stock_trading_server.repo;

import com.deep.stock_trading_server.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepo extends JpaRepository<Stock, Long> {
    Stock findByStockSymbol(String stockSymbol);
}
