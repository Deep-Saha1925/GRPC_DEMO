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
		String stock = "AAPL";
		try{
			System.out.println(stockClientService.getStockPrice(stock));
		} catch (Exception e) {
			throw new RuntimeException("Not found with stock name " + stock);
		}
	}
}
