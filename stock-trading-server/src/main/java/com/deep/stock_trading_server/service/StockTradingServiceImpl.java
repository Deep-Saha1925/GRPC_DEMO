package com.deep.stock_trading_server.service;

import com.deep.StockRequest;
import com.deep.StockResponse;
import com.deep.StockTradingServiceGrpc;
import com.deep.stock_trading_server.entity.Stock;
import com.deep.stock_trading_server.repo.StockRepo;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
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
}
