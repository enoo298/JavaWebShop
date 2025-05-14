package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.LoginHistory;
import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.repo.LoginHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LoginHistoryService {

    private final LoginHistoryRepository historyRepo;

    public LoginHistoryService(LoginHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    public LoginHistory record(User user, String ip) {
        LoginHistory entry = new LoginHistory();
        entry.setUser(user);
        entry.setIpAddress(ip);
        return historyRepo.save(entry);
    }

    public List<LoginHistory> getAll() {
        return historyRepo.findAll();
    }

    public List<LoginHistory> getByUser(User user) {
        return historyRepo.findByUser(user);
    }

    public List<LoginHistory> getByPeriod(LocalDateTime from, LocalDateTime to) {
        // možeš dopuniti repozitorijem findByLoginTimestampBetween
        return historyRepo.findAll().stream()
                .filter(h -> !h.getLoginTimestamp().isBefore(from) &&
                        !h.getLoginTimestamp().isAfter(to))
                .toList();
    }
}
