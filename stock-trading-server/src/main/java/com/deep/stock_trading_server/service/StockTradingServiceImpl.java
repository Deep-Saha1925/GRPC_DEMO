package com.deep.stock_trading_server.service;

import com.deep.StockRequest;
import com.deep.StockResponse;
import com.deep.StockTradingServiceGrpc;
import com.deep.stock_trading_server.entity.Stock;
import com.deep.stock_trading_server.repo.StockRepo;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

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
}
