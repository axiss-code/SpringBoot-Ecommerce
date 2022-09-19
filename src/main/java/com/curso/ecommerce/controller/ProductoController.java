package com.curso.ecommerce.controller;

import java.io.IOException;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IProductoService;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.UploadFileService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private UploadFileService upload;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@GetMapping("")
	public String show(Model model) {
		//por defecto, la ruta en la que busca es /src/main/resources/templates
		model.addAttribute("productos",productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/create")
	public String create() {
		return "productos/create";
	}
		
	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		//LOGGER.info("El objeto producto: {}",producto);
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("usuario_id").toString())).get();
		producto.setUsuario(u);
		
		if (producto.getId()==null) { 		//entra al crearse el producto
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		} 
		productoService.save(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Producto producto = new Producto();
		Optional <Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		LOGGER.info("El producto buscado: {}",producto);
		model.addAttribute("producto", producto);
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
		
		//procesa si hay cambio de imagen
		Producto p = new Producto();
		p = productoService.get(producto.getId()).get();
		
		if(file.isEmpty()) { 							//entra cuando NO cambiamos la imagen existente
			producto.setImagen(p.getImagen());
		} else {  										//entra cuando SI editamos la imagen existente, borrando la anterior
			if (!p.getImagen().equals("no_image.jpg")) { //Para que no borre no_image.jpg
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		producto.setUsuario(p.getUsuario());
		productoService.update(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String update(@PathVariable Integer id) {
		//elimina la img guardada en la carpeta images
		Producto p = new Producto();
		p=productoService.get(id).get();
		if (!p.getImagen().equals("no_image.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		productoService.delete(id);
		return "redirect:/productos";
	}

}
