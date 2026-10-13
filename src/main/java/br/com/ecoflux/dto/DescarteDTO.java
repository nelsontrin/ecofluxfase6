package br.com.ecoflux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DescarteDTO {

    @NotNull(message = "O ID do ponto de coleta é obrigatório")
    private Long pontoColetaId;

    @NotBlank(message = "O tipo de resíduo é obrigatório")
    private String tipoResiduo;

    @NotNull(message = "O volume é obrigatório")
    @Positive(message = "O volume deve ser maior que zero")
    private Double volume;

    @NotNull(message = "A informação se é reciclável é obrigatória")
    private Boolean reciclavel;
}
