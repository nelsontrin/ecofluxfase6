package br.com.ecoflux.controller;

import br.com.ecoflux.dto.ColetaDTO;
import br.com.ecoflux.model.Coleta;
import br.com.ecoflux.service.ColetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coletas")
public class ColetaController {

    @Autowired
    private ColetaService service;

    @GetMapping
    public ResponseEntity<List<Coleta>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Coleta> registrarColeta(@Valid @RequestBody ColetaDTO dto) {
        return new ResponseEntity<>(service.registrarColeta(dto), HttpStatus.CREATED);
    }
}
