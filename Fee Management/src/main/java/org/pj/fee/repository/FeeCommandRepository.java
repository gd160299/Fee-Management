package org.pj.fee.repository;

import org.pj.fee.entity.FeeCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeCommandRepository extends JpaRepository<FeeCommand, Long> {
}
