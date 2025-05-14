package hr.algebra.javawebshop.repo;

import hr.algebra.javawebshop.models.LoginHistory;
import hr.algebra.javawebshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUser(User user);
   // List<LoginHistory> findByLoginTimestampBetween(LocalDateTime start, LocalDateTime end);
   // List<LoginHistory> findByIpAddress(String ipAddress);
}