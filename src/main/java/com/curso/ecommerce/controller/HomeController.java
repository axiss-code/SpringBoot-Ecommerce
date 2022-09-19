package com.curso.ecommerce.controller;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.curso.ecommerce.model.DetalleOrden;
import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IDetalleOrdenService;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IProductoService;
import com.curso.ecommerce.service.IUsuarioService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService; 
	
	@Autowired
	private IOrdenService ordenService;
	
	@Autowired
	private IDetalleOrdenService detalleOrdenService;
	
	private Orden orden = new Orden();
	private List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();
	
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		//LOGGER.info("Session del usuario: {}", session.getAttribute("usuario_id"));
		
		model.addAttribute("productos", productoService.findAll());
		model.addAttribute("sesion", session.getAttribute("usuario_id"));
		return "usuario/home";
	}
	
	@GetMapping("productohome/{id}")
	public String home(@PathVariable Integer id, Model model, HttpSession session) {
		Producto producto = new Producto();
		Optional <Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		//LOGGER.info("El id del producto enviado: {}",id);
		model.addAttribute("producto", producto);
		model.addAttribute("sesion", session.getAttribute("usuario_id"));
		return "usuario/productohome";
	}
	
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model, HttpSession session) {
		
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;

		Optional<Producto>optionalProducto = productoService.get(id);
		//LOGGER.info("Producto añadido: {}",optionalProducto.get());
		//LOGGER.info("Cantidad añadida: {}",cantidad);
		producto = optionalProducto.get();
		
		detalleOrden.setProducto(producto);
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setTotal(producto.getPrecio() * cantidad);
		
		Integer idLookup = producto.getId();
		boolean isAdded = detalles.stream().anyMatch(p -> p.getProducto().getId() == idLookup);
		if (!isAdded) {
			detalles.add(detalleOrden);
		} else {
			for (DetalleOrden dtor : detalles) {
				if (dtor.getProducto().getId() == idLookup) { 
					dtor.setCantidad(cantidad + dtor.getCantidad());
					dtor.setTotal(dtor.getCantidad() * dtor.getPrecio());
				}
			}
		}
		
		sumaTotal = detalles.stream().mapToDouble(d -> d.getTotal()).sum();
		orden.setTotal(sumaTotal);
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("sesion", session.getAttribute("usuario_id"));
		return "usuario/carrito";
	}
	
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		
		List<DetalleOrden> detallesNueva = new ArrayList<DetalleOrden>();
		for(DetalleOrden dtor : detalles) {
			if(dtor.getProducto().getId() != id) 
				detallesNueva.add(dtor);
		}
		
		detalles=detallesNueva;
		double sumaTotal = detalles.stream().mapToDouble(d -> d.getTotal()).sum();
		orden.setTotal(sumaTotal);
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/carrito";
	}
	
	@GetMapping("/getCart")
	public String getCart (Model model, HttpSession session) {
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("sesion",session.getAttribute("usuario_id"));
		return "usuario/carrito";
	}
	
	@GetMapping("/order")
	public String order (Model model, HttpSession session) {
		//LOGGER.info("get = {}", session.getAttribute("usuario_id"));
		
		if (session.getAttribute("usuario_id") == null) {
			return "redirect:/usuario/login";
		}
		
		Optional<Usuario> usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("usuario_id").toString()));
		
		if(usuario.isPresent()) {
			model.addAttribute("cart", detalles);
			model.addAttribute("orden", orden);
			model.addAttribute("usuario", usuario.get());
			model.addAttribute("sesion", session.getAttribute("usuario_id"));
			return "usuario/resumenorden";
		} else {
			return "redirect:/";
		}
	}
	
	@GetMapping("/saveOrder")
	public String saveOrder (HttpSession session) {
		orden.setFechaCreacion(LocalDate.now());
		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setUsuario(usuarioService.findById(Integer.parseInt(session.getAttribute("usuario_id").toString())).get());
		ordenService.save(orden);
		for(DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		this.orden = new Orden();
		detalles.clear();
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProduct (@RequestParam String nombre, Model model, HttpSession session) {
		//LOGGER.info("search nombre: {}", nombre);
		List<Producto> productos=productoService.findAll().stream().filter(n -> n.getNombre().toLowerCase().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		model.addAttribute("sesion", session.getAttribute("usuario_id"));
		return "usuario/home";
	}
	
}