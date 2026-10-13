package br.com.ecoflux.service;

import br.com.ecoflux.dto.DescarteDTO;
import br.com.ecoflux.exception.BusinessException;
import br.com.ecoflux.model.Alerta;
import br.com.ecoflux.model.Descarte;
import br.com.ecoflux.model.PontoColeta;
import br.com.ecoflux.repository.AlertaRepository;
import br.com.ecoflux.repository.DescarteRepository;
import br.com.ecoflux.repository.PontoColetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DescarteService {

    @Autowired
    private DescarteRepository descarteRepository;

    @Autowired
    private PontoColetaService pontoColetaService;

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    public List<Descarte> listarTodos() {
        return descarteRepository.findAll();
    }

    public List<Descarte> listarPorPontoColeta(Long pontoColetaId) {
        return descarteRepository.findByPontoColetaId(pontoColetaId);
    }

    @Transactional
    public Descarte registrarDescarte(DescarteDTO dto) {
        PontoColeta ponto = pontoColetaService.buscarPorId(dto.getPontoColetaId());

        if (!ponto.getAtivo()) {
            throw new BusinessException("Não é possível registrar descarte em um ponto de coleta inativo.");
        }

        if (ponto.getVolumeAtual() + dto.getVolume() > ponto.getCapacidadeMaxima()) {
            throw new BusinessException("O volume do descarte excede a capacidade máxima do ponto de coleta.");
        }

        Descarte descarte = new Descarte();
        descarte.setPontoColeta(ponto);
        descarte.setTipoResiduo(dto.getTipoResiduo());
        descarte.setVolume(dto.getVolume());
        descarte.setReciclavel(dto.getReciclavel());
        descarte.setDataHora(LocalDateTime.now());

        // Atualiza volume do ponto
        ponto.setVolumeAtual(ponto.getVolumeAtual() + dto.getVolume());
        pontoColetaRepository.save(ponto);

        // Verifica se atingiu 85% da capacidade para gerar alerta
        double percentualOcupacao = (ponto.getVolumeAtual() / ponto.getCapacidadeMaxima()) * 100;
        if (percentualOcupacao >= 85.0) {
            gerarAlertaCapacidade(ponto, percentualOcupacao);
        }

        return descarteRepository.save(descarte);
    }

    private void gerarAlertaCapacidade(PontoColeta ponto, double percentual) {
        Alerta alerta = new Alerta();
        alerta.setPontoColeta(ponto);
        alerta.setMensagem(String.format("Ponto de coleta %s atingiu %.2f%% da capacidade.", ponto.getNome(), percentual));
        alerta.setNivel(percentual >= 95.0 ? "ALTO" : "MEDIO");
        alerta.setDataHora(LocalDateTime.now());
        alerta.setResolvido(false);
        alertaRepository.save(alerta);
    }
}
