package com.curso.ecommerce.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.repository.IOrdenRepository;

@Service
public class OrdenServiceImpl implements IOrdenService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(OrdenServiceImpl.class);
	
	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public Orden save(Orden orden) {
		return ordenRepository.save(orden);
	}

	@Override
	public List<Orden> findAll() {
		return ordenRepository.findAll();
	}
	
	@Override
	public String generarNumeroOrden() {
		Integer max;
		String numOrden;
		List<Orden> ordenes = findAll();
		if (ordenes.size()==0) {
			max=1;
			//LOGGER.info("El max es 0");
		} else {
			max = ordenes.stream().mapToInt(o -> Integer.parseInt(o.getNumero())).max().getAsInt();
			//LOGGER.info("El max es : {}", max);
			max++;
		}
		numOrden = String.format("%06d",max).toString();
		//LOGGER.info("El sig es : {}", numOrden);
		return numOrden;
	}

	@Override
	public List<Orden> findByUsuario(Usuario usuario) {
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		return ordenRepository.findById(id);
	}
}
