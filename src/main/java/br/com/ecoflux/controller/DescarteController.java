package br.com.ecoflux.controller;

import br.com.ecoflux.dto.DescarteDTO;
import br.com.ecoflux.model.Descarte;
import br.com.ecoflux.service.DescarteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/descartes")
public class DescarteController {

    @Autowired
    private DescarteService service;

    @GetMapping
    public ResponseEntity<List<Descarte>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/ponto/{id}")
    public ResponseEntity<List<Descarte>> listarPorPontoColeta(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorPontoColeta(id));
    }

    @PostMapping
    public ResponseEntity<Descarte> registrarDescarte(@Valid @RequestBody DescarteDTO dto) {
        return new ResponseEntity<>(service.registrarDescarte(dto), HttpStatus.CREATED);
    }
}
