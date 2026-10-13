package br.com.ecoflux.service;

import br.com.ecoflux.dto.PontoColetaDTO;
import br.com.ecoflux.exception.ResourceNotFoundException;
import br.com.ecoflux.model.PontoColeta;
import br.com.ecoflux.repository.PontoColetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PontoColetaService {

    @Autowired
    private PontoColetaRepository repository;

    public List<PontoColeta> listarTodos() {
        return repository.findAll();
    }

    public PontoColeta buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta não encontrado com id: " + id));
    }

    public PontoColeta criar(PontoColetaDTO dto) {
        PontoColeta ponto = new PontoColeta();
        ponto.setNome(dto.getNome());
        ponto.setEndereco(dto.getEndereco());
        ponto.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        ponto.setVolumeAtual(0.0);
        ponto.setAtivo(true);
        return repository.save(ponto);
    }

    public PontoColeta atualizar(Long id, PontoColetaDTO dto) {
        PontoColeta ponto = buscarPorId(id);
        ponto.setNome(dto.getNome());
        ponto.setEndereco(dto.getEndereco());
        ponto.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        return repository.save(ponto);
    }

    public void deletar(Long id) {
        PontoColeta ponto = buscarPorId(id);
        ponto.setAtivo(false);
        repository.save(ponto);
    }
}
