package br.com.ecoflux.repository;

import br.com.ecoflux.model.PontoColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {
}
