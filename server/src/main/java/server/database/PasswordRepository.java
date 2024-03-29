package server.database;

import commons.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Debt, Long>  {
}
