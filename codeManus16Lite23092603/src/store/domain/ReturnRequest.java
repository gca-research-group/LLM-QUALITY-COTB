package store.domain;
import java.util.Map;
public record ReturnRequest(String id, String orderId, Map<String,Integer> items, double refundTotal) {}
