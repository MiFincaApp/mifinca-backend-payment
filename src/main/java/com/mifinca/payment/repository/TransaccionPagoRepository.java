
package com.mifinca.payment.repository;

import com.mifinca.payment.entity.TransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {
    Optional<TransaccionPago> findByIdTransaccionWompi(String idTransaccionWompi);
}
