package br.com.ecoflux.repository;

import br.com.ecoflux.model.Coleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColetaRepository extends JpaRepository<Coleta, Long> {
    List<Coleta> findByPontoColetaId(Long pontoColetaId);
}
