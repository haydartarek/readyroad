package com.readyroad.readyroadbackend.payment;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface UserEntitlementRepository extends JpaRepository<UserEntitlement, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from UserEntitlement e where e.userId = :userId")
    Optional<UserEntitlement> lockByUserId(@Param("userId") Long userId);
}
