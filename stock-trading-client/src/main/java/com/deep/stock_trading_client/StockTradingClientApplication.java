package com.deep.stock_trading_client;

import com.deep.stock_trading_client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLOutput;

@SpringBootApplication
public class StockTradingClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

	private final StockClientService stockClientService;

	public StockTradingClientApplication(StockClientService stockClientService) {
		this.stockClientService = stockClientService;
	}

	@Override
	public void run(String... args) throws Exception {
		String stock = "AMZN";
		try{
//			stockClientService.subScribeStockPrice(stock);
			stockClientService.placeBulkOrders();
		} catch (Exception e) {
			throw new RuntimeException("Not found with stock name " + stock);
		}
	}
}
