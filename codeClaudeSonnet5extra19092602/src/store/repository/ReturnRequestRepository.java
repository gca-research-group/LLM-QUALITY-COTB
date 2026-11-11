package store.repository;

import store.model.ReturnRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReturnRequestRepository {
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();

    public void add(ReturnRequest returnRequest) {
        returns.put(returnRequest.getId(), returnRequest);
    }

    public ReturnRequest findById(String id) {
        return returns.get(id);
    }

    public List<ReturnRequest> findAll() {
        return new ArrayList<>(returns.values());
    }
}