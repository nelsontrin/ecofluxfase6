package br.com.ecoflux.service;

import br.com.ecoflux.model.Alerta;
import br.com.ecoflux.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository repository;

    public List<Alerta> listarTodos() {
        return repository.findAll();
    }

    public List<Alerta> listarNaoResolvidos() {
        return repository.findByResolvidoFalse();
    }
}
