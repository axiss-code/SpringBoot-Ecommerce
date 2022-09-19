package com.curso.ecommerce.service;

import java.util.List;
import java.util.Optional;
import com.curso.ecommerce.model.Producto;

public interface IProductoService {
	//aqui definiremos todos los metodos CRUD 	
	
	public Producto save (Producto producto);
	public Optional<Producto> get (Integer id); //Optional porque no sabemos si existe en la DB
	public void update (Producto producto);
	public void delete (Integer id);
	public List<Producto> findAll();
	
}
