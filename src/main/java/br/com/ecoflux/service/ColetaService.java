package br.com.ecoflux.service;

import br.com.ecoflux.dto.ColetaDTO;
import br.com.ecoflux.exception.BusinessException;
import br.com.ecoflux.model.Alerta;
import br.com.ecoflux.model.Coleta;
import br.com.ecoflux.model.PontoColeta;
import br.com.ecoflux.repository.AlertaRepository;
import br.com.ecoflux.repository.ColetaRepository;
import br.com.ecoflux.repository.PontoColetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ColetaService {

    @Autowired
    private ColetaRepository coletaRepository;

    @Autowired
    private PontoColetaService pontoColetaService;

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    public List<Coleta> listarTodas() {
        return coletaRepository.findAll();
    }

    @Transactional
    public Coleta registrarColeta(ColetaDTO dto) {
        PontoColeta ponto = pontoColetaService.buscarPorId(dto.getPontoColetaId());

        if (dto.getVolumeColetado() > ponto.getVolumeAtual()) {
            throw new BusinessException("O volume coletado não pode ser maior que o volume atual do ponto.");
        }

        Coleta coleta = new Coleta();
        coleta.setPontoColeta(ponto);
        coleta.setEmpresaResponsavel(dto.getEmpresaResponsavel());
        coleta.setVolumeColetado(dto.getVolumeColetado());
        coleta.setDestinoFinal(dto.getDestinoFinal());
        coleta.setDataHora(LocalDateTime.now());

        // Atualiza volume do ponto
        ponto.setVolumeAtual(ponto.getVolumeAtual() - dto.getVolumeColetado());
        pontoColetaRepository.save(ponto);

        // Resolve alertas pendentes do ponto
        List<Alerta> alertas = alertaRepository.findByPontoColetaId(ponto.getId());
        for (Alerta alerta : alertas) {
            if (!alerta.getResolvido()) {
                alerta.setResolvido(true);
                alertaRepository.save(alerta);
            }
        }

        return coletaRepository.save(coleta);
    }
}
