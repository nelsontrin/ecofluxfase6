package br.com.ecoflux.service;

import br.com.ecoflux.dto.DescarteDTO;
import br.com.ecoflux.exception.BusinessException;
import br.com.ecoflux.model.PontoColeta;
import br.com.ecoflux.repository.AlertaRepository;
import br.com.ecoflux.repository.DescarteRepository;
import br.com.ecoflux.repository.PontoColetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescarteServiceTest {

    @Mock
    private DescarteRepository descarteRepository;

    @Mock
    private PontoColetaService pontoColetaService;

    @Mock
    private PontoColetaRepository pontoColetaRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private DescarteService service;

    @Test
    void deveAtualizarVolumeEGerarAlertaAoAtingirOitentaECincoPorCento() {
        PontoColeta ponto = ponto(80.0, 100.0, true);
        DescarteDTO dto = descarte(1L, 5.0);
        when(pontoColetaService.buscarPorId(1L)).thenReturn(ponto);
        when(descarteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.registrarDescarte(dto);

        assertEquals(85.0, ponto.getVolumeAtual());
        verify(pontoColetaRepository).save(ponto);
        ArgumentCaptor<br.com.ecoflux.model.Alerta> alerta = ArgumentCaptor.forClass(br.com.ecoflux.model.Alerta.class);
        verify(alertaRepository).save(alerta.capture());
        assertEquals("MEDIO", alerta.getValue().getNivel());
        assertEquals(false, alerta.getValue().getResolvido());
    }

    @Test
    void naoDevePermitirDescarteAcimaDaCapacidade() {
        PontoColeta ponto = ponto(95.0, 100.0, true);
        when(pontoColetaService.buscarPorId(1L)).thenReturn(ponto);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.registrarDescarte(descarte(1L, 6.0)));

        assertEquals("O volume do descarte excede a capacidade máxima do ponto de coleta.", exception.getMessage());
    }

    @Test
    void naoDevePermitirDescarteEmPontoInativo() {
        PontoColeta ponto = ponto(0.0, 100.0, false);
        when(pontoColetaService.buscarPorId(1L)).thenReturn(ponto);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.registrarDescarte(descarte(1L, 5.0)));

        assertEquals("Não é possível registrar descarte em um ponto de coleta inativo.", exception.getMessage());
    }

    private PontoColeta ponto(double volume, double capacidade, boolean ativo) {
        PontoColeta ponto = new PontoColeta();
        ponto.setId(1L);
        ponto.setNome("Ponto Centro");
        ponto.setVolumeAtual(volume);
        ponto.setCapacidadeMaxima(capacidade);
        ponto.setAtivo(ativo);
        return ponto;
    }

    private DescarteDTO descarte(Long pontoId, double volume) {
        DescarteDTO dto = new DescarteDTO();
        dto.setPontoColetaId(pontoId);
        dto.setTipoResiduo("PLASTICO");
        dto.setVolume(volume);
        dto.setReciclavel(true);
        return dto;
    }
}
