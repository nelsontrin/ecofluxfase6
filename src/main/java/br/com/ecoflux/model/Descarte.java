package br.com.ecoflux.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_descarte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Descarte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ponto_coleta_id", nullable = false)
    private PontoColeta pontoColeta;

    @Column(nullable = false, length = 50)
    private String tipoResiduo;

    @Column(nullable = false)
    private Double volume;

    @Column(nullable = false)
    private Boolean reciclavel;

    @Column(nullable = false)
    private LocalDateTime dataHora;
}
