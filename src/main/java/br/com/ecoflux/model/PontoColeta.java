package br.com.ecoflux.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_ponto_coleta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false)
    private Double capacidadeMaxima;

    @Column(nullable = false)
    private Double volumeAtual;

    @Column(nullable = false)
    private Boolean ativo;
}
