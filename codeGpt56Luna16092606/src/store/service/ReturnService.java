package store.service;

import java.util.ArrayList;
import java.util.List;
import store.model.CashflowEntry;
import store.model.CashflowType;
import store.model.Order;
import store.model.OrderStatus;
import store.model.ReturnRequest;

public class ReturnService {
    private final List<ReturnRequest> returns = new ArrayList<ReturnRequest>();
    private final StockService stock;
    private final CashflowService cashflow;

    public ReturnService(StockService stock, CashflowService cashflow) {
        this.stock = stock;
        this.cashflow = cashflow;
    }

    public ReturnRequest create(String id, Order order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        ReturnRequest request = new ReturnRequest(id, order);
        returns.add(request);
        return request;
    }

    public void process(ReturnRequest request) {
        if (request.getItems().isEmpty()) {
            throw new IllegalStateException("Return must contain items");
        }

        if (request.isProcessed()) {
            throw new IllegalStateException("Return already processed");
        }

        for (store.model.ReturnItem item : request.getItems()) {
            stock.increase(item.getProduct(), item.getQuantity());
        }

        double total = request.getRefundTotal();

        cashflow.record(new CashflowEntry(
            "REF-" + request.getId(),
            CashflowType.OUTFLOW,
            total,
            "Refund for " + request.getOrder().getId()
        ));

        request.markProcessed();
    }

    public List<ReturnRequest> all() {
        return returns;
    }
}