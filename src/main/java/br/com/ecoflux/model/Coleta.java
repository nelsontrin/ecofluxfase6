package br.com.ecoflux.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_coleta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ponto_coleta_id", nullable = false)
    private PontoColeta pontoColeta;

    @Column(nullable = false, length = 100)
    private String empresaResponsavel;

    @Column(nullable = false)
    private Double volumeColetado;

    @Column(nullable = false, length = 100)
    private String destinoFinal;

    @Column(nullable = false)
    private LocalDateTime dataHora;
}
