package store.repository;

import store.model.Payment;

public class PaymentRepository extends InMemoryRepository<Payment> {

    public PaymentRepository() {
        super("Payment");
    }

    @Override
    protected String extractId(Payment entity) {
        return entity.getId();
    }
}