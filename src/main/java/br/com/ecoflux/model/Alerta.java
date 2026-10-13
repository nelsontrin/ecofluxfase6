package br.com.ecoflux.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_alerta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ponto_coleta_id", nullable = false)
    private PontoColeta pontoColeta;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @Column(nullable = false, length = 20)
    private String nivel; // ALTO, MEDIO, BAIXO

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private Boolean resolvido;
}
