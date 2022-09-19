package com.curso.ecommerce.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IProductoService;
import com.curso.ecommerce.service.IUsuarioService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private IProductoService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("")
	public String home(Model model) {
		// por defecto, la ruta en la que busca es /src/main/resources/templates

		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);

		return "administrador/home";
	}

	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
	}

	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes", ordenService.findAll());
		return "administrador/ordenes";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(@PathVariable Integer id, Model model) {
		//LOGGER.info("id de la orden: {}", id);
		Orden ordet = ordenService.findById(id).get();
		model.addAttribute("detalles", ordet.getDetalle());
		return "administrador/detalleorden";
	}
	
	@PostMapping("/search")
	public String searchProduct (@RequestParam String nombre, Model model, HttpSession session) {
		//LOGGER.info("search nombre: {}", nombre);
		List<Producto> productos=productoService.findAll().stream().filter(n -> n.getNombre().toLowerCase().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		model.addAttribute("sesion", session.getAttribute("usuario_id"));
		return "administrador/home";
	}
	
	@GetMapping("productoview/{id}")
	public String home(@PathVariable Integer id, Model model) {
		Producto producto = new Producto();
		Optional <Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		//LOGGER.info("El id del producto enviado: {}",id);
		model.addAttribute("producto", producto); //lo enviamos hacia la vista
		return "administrador/productoview";
	}
}
