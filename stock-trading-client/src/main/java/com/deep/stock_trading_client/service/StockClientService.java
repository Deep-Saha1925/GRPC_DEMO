package com.deep.stock_trading_client.service;

import com.deep.*;
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

    public void placeBulkOrders(){
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary summary) {
                System.out.println("Total Order: " + summary.getTotalOrder());
                System.out.println("Total Amount: " + summary.getTotalAmount());
                System.out.println("Total Success Orders: " + summary.getSuccessCount());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Order summary completed..");
            }
        };

        StreamObserver<StockOrder> requestObserver = serviceStub.bulkStockOrder(responseObserver);
        try{
            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("ORD12345")
                    .setStockSymbol("AAPL")
                    .setQuantity(100)
                    .setPrice(189.75)
                    .setOrderType("BUY") // could be "SELL" also
                    .build()
            );


            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("ORD67890")
                            .setStockSymbol("GOOG")
                            .setQuantity(50)
                            .setPrice(2750.50)
                            .setOrderType("SELL")
                            .build()
            );

            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("ORD67892")
                            .setStockSymbol("TATA")
                            .setQuantity(20)
                            .setPrice(270.50)
                            .setOrderType("BUY")
                            .build()
            );

            requestObserver.onCompleted();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startTrading() throws InterruptedException {
        StreamObserver<StockOrder> streamObserver = serviceStub.liveTrading(new StreamObserver<>() {
            @Override
            public void onNext(TradeStatus tradeStatus) {
                System.out.println("server response " + tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("stream completed");
            }
        });

        //sending multiple orders
        for (int i = 0; i < 10; i++) {
            streamObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("ORDER-"+i)
                            .setStockSymbol("APPL")
                            .setQuantity(i*10)
                            .setPrice(150.0+i)
                            .setOrderType("BUY")
                            .build()
            );
            Thread.sleep(1000);
        }
        streamObserver.onCompleted();
    }
}
