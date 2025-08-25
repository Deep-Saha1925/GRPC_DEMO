package com.deep.stock_trading_server.service;

import com.deep.*;
import com.deep.stock_trading_server.entity.Stock;
import com.deep.stock_trading_server.repo.StockRepo;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    private final StockRepo stockRepo;

    public StockTradingServiceImpl(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        String stockSymbol = request.getStockSymbol();
        Stock stockFromDB = stockRepo.findByStockSymbol(stockSymbol);

        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockSymbol)
                .setPrice(stockFromDB.getPrice())
                .setTimestamp(stockFromDB.getLastUpdated())
                .build();

        responseObserver.onNext(stockResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        try{
            String symbol = request.getStockSymbol();
            for (int i = 0; i <=10; i++) {
                StockResponse stockResponse = StockResponse.newBuilder()
                        .setStockSymbol(symbol)
                        .setPrice(new Random().nextDouble(200))
                        .setTimestamp(Instant.now().toString())
                        .build();

                responseObserver.onNext(stockResponse);
                TimeUnit.SECONDS.sleep(1);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {
        return new StreamObserver<StockOrder>() {

            private int totalOrders = 0;
            private double totalAmount = 0;
            private int successCount = 0;

            @Override
            public void onNext(StockOrder stockOrder) {
                totalOrders++;
                totalAmount += stockOrder.getQuantity() * stockOrder.getPrice();
                successCount++;
                System.out.println("received order.." + stockOrder);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error in stock order..");
            }

            @Override
            public void onCompleted() {
                OrderSummary summary = OrderSummary.newBuilder()
                                .setSuccessCount(successCount)
                                        .setTotalAmount(totalAmount)
                                                .setTotalOrder(totalOrders+"")
                                                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();
                System.out.println("Summary created..");
            }
        };
    }


    @Override
    public StreamObserver<StockOrder> liveTrading(StreamObserver<TradeStatus> responseObserver) {
        return new StreamObserver<StockOrder>() {
            @Override
            public void onNext(StockOrder stockOrder) {
                System.out.println("received order " + stockOrder);
                String status = "EXECUTED";
                String message = "Order placed successfully";
                if(stockOrder.getQuantity() <= 0){
                    status = "FAILED";
                    message = "Invalid quantity";
                }

                TradeStatus tradeStatus = TradeStatus.newBuilder()
                        .setOrderId(stockOrder.getOrderId())
                        .setMessage(message)
                        .setStatus(status)
                        .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build();

                responseObserver.onNext(tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error " + throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
