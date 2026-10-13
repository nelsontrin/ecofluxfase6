package br.com.ecoflux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PontoColetaDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

    @NotNull(message = "A capacidade máxima é obrigatória")
    @Positive(message = "A capacidade máxima deve ser maior que zero")
    private Double capacidadeMaxima;
}
