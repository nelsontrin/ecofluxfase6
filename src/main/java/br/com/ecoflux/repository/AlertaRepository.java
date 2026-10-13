package br.com.ecoflux.repository;

import br.com.ecoflux.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByPontoColetaId(Long pontoColetaId);
    List<Alerta> findByResolvidoFalse();
}
