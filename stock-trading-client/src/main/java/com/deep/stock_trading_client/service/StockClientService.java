package com.deep.stock_trading_client.service;

import com.deep.StockRequest;
import com.deep.StockResponse;
import com.deep.StockTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {



//    @GrpcClient("stockService")
//    private StockTradingServiceGrpc.StockTradingServiceBlockingStub serviceBlockingStub;
//
//    public StockResponse getStockPrice(String stockSymbol){
//
//        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
//        return serviceBlockingStub.getStockPrice(request);
//    }

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub serviceStub;

    public void subScribeStockPrice(String stockSymbol){
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        serviceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("Stock price update: " + stockResponse.getStockSymbol() +
                        " Price: " + stockResponse.getPrice() +
                        " Time: " + stockResponse.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error in stockPrice");
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock call done..");
            }
        });
    }
}
