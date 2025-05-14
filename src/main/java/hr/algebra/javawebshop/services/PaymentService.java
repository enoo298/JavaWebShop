package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.Order;
import hr.algebra.javawebshop.models.Payment;
import hr.algebra.javawebshop.repo.OrderRepository;
import hr.algebra.javawebshop.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository   orderRepo;

    public PaymentService(PaymentRepository paymentRepo,
                          OrderRepository orderRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo   = orderRepo;
    }

    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    public Payment getPayment(Long id) {
        return paymentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public Payment getByOrder(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return paymentRepo.findByOrder(o)
                .orElseThrow(() -> new IllegalStateException("No payment for this order"));
    }

    public Payment createPayment(Payment data) {
        Long oid = data.getOrder().getId();
        Order o = orderRepo.findById(oid)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (paymentRepo.findByOrder(o).isPresent()) {
            throw new IllegalStateException("Payment already exists for order " + oid);
        }
        data.setOrder(o);
        return paymentRepo.save(data);
    }

    public Payment updatePayment(Long id, String status) {
        Payment existing = getPayment(id);
        existing.setStatus(status);
        return paymentRepo.save(existing);
    }

    public void deletePayment(Long id) {
        if (!paymentRepo.existsById(id)) {
            throw new IllegalArgumentException("Payment not found");
        }
        paymentRepo.deleteById(id);
    }
}
