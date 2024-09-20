package org.pj.fee.Repository;

import org.pj.fee.Entity.FeeCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeCommandRepository extends JpaRepository<FeeCommand, Long> {
}
