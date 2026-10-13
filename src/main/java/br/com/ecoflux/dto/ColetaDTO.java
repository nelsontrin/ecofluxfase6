package br.com.ecoflux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ColetaDTO {

    @NotNull(message = "O ID do ponto de coleta é obrigatório")
    private Long pontoColetaId;

    @NotBlank(message = "A empresa responsável é obrigatória")
    private String empresaResponsavel;

    @NotNull(message = "O volume coletado é obrigatório")
    @Positive(message = "O volume coletado deve ser maior que zero")
    private Double volumeColetado;

    @NotBlank(message = "O destino final é obrigatório")
    private String destinoFinal;
}
