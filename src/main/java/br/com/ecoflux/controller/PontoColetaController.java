package br.com.ecoflux.controller;

import br.com.ecoflux.dto.PontoColetaDTO;
import br.com.ecoflux.model.PontoColeta;
import br.com.ecoflux.service.PontoColetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pontos-coleta")
public class PontoColetaController {

    @Autowired
    private PontoColetaService service;

    @GetMapping
    public ResponseEntity<List<PontoColeta>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PontoColeta> criar(@Valid @RequestBody PontoColetaDTO dto) {
        return new ResponseEntity<>(service.criar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColeta> atualizar(@PathVariable Long id, @Valid @RequestBody PontoColetaDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
