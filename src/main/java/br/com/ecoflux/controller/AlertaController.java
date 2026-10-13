package br.com.ecoflux.controller;

import br.com.ecoflux.model.Alerta;
import br.com.ecoflux.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    @Autowired
    private AlertaService service;

    @GetMapping
    public ResponseEntity<List<Alerta>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Alerta>> listarNaoResolvidos() {
        return ResponseEntity.ok(service.listarNaoResolvidos());
    }
}
