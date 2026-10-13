package br.com.ecoflux.repository;

import br.com.ecoflux.model.Descarte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescarteRepository extends JpaRepository<Descarte, Long> {
    List<Descarte> findByPontoColetaId(Long pontoColetaId);
}
