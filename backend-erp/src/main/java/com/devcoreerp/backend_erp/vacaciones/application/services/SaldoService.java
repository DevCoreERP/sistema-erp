package com.devcoreerp.backend_erp.vacaciones.application.services;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence.SaldoRepository;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSaldoDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSaldoDTO;
import com.devcoreerp.backend_erp.vacaciones.application.mappers.SaldoMapper;
import com.devcoreerp.backend_erp.vacaciones.domain.Saldo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class SaldoService{

    private final SaldoRepository saldoRepository;

    public SaldoService(SaldoRepository saldoRepository){
        this.saldoRepository = saldoRepository;
    }

    public ResponseSaldoDTO create(CreateSaldoDTO dto){
        Saldo saldo = SaldoMapper.toEntity(dto, dto.usuarioId()
        );
        Saldo saved = saldoRepository.save(saldo);
        return SaldoMapper.toDTO(saved);
    }

    public ResponseSaldoDTO update(Long id, CreateSaldoDTO dto){
        Saldo saldo = saldoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Saldo no encontrada"
        ));
        saldo.setDias(dto.dias());
        saldo.setUpdatedAt(new Date());
        Saldo updated = saldoRepository.save(saldo);
        return SaldoMapper.toDTO(updated);
    }

    public ResponseSaldoDTO delete(Long id){
        Saldo saldo = saldoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Saldo no encontrada"
        ));
        saldoRepository.delete(saldo);
        return SaldoMapper.toDTO(saldo);
    }

    @Transactional(readOnly = true)
    public ResponseSaldoDTO findById(Long id) {
        Saldo saldo = saldoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Saldo no encontrada"
        ));
        return SaldoMapper.toDTO(saldo);
    }

    @Transactional(readOnly = true)
    public List<ResponseSaldoDTO> findAll() {
        List<Saldo> list = saldoRepository.findAll();
        return list.stream().map(SaldoMapper::toDTO).toList();
    }
}