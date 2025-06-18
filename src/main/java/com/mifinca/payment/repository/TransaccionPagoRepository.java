
package com.mifinca.payment.repository;

import com.mifinca.payment.entity.TransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {
    
}
