package br.com.ecoflux.service;

import br.com.ecoflux.dto.ColetaDTO;
import br.com.ecoflux.model.Alerta;
import br.com.ecoflux.model.PontoColeta;
import br.com.ecoflux.repository.AlertaRepository;
import br.com.ecoflux.repository.ColetaRepository;
import br.com.ecoflux.repository.PontoColetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColetaServiceTest {

    @Mock
    private ColetaRepository coletaRepository;

    @Mock
    private PontoColetaService pontoColetaService;

    @Mock
    private PontoColetaRepository pontoColetaRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private ColetaService service;

    @Test
    void deveReduzirVolumeEResolverAlertasPendentes() {
        PontoColeta ponto = new PontoColeta();
        ponto.setId(1L);
        ponto.setNome("Ponto Centro");
        ponto.setVolumeAtual(100.0);
        ponto.setCapacidadeMaxima(100.0);
        ponto.setAtivo(true);

        Alerta alerta = new Alerta();
        alerta.setPontoColeta(ponto);
        alerta.setResolvido(false);

        ColetaDTO dto = new ColetaDTO();
        dto.setPontoColetaId(1L);
        dto.setEmpresaResponsavel("EcoLogística");
        dto.setVolumeColetado(40.0);
        dto.setDestinoFinal("Cooperativa de reciclagem");

        when(pontoColetaService.buscarPorId(1L)).thenReturn(ponto);
        when(alertaRepository.findByPontoColetaId(1L)).thenReturn(List.of(alerta));
        when(coletaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.registrarColeta(dto);

        assertEquals(60.0, ponto.getVolumeAtual());
        assertEquals(true, alerta.getResolvido());
        verify(pontoColetaRepository).save(ponto);
        verify(alertaRepository).save(alerta);
    }
}
