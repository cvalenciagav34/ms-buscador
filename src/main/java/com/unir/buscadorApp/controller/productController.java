package com.unir.buscadorApp.controller;

import com.unir.buscadorApp.entity.pojo.Product;
import com.unir.buscadorApp.entity.request.ProductRequest;
import com.unir.buscadorApp.entity.response.ProductsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre productos alojadas en elmotor de búsqueda y analítica Elasticsearch.")
public class productController {
    //@Autowired
    private final com.unir.buscadorApp.service.ProductService ProductService;

    @GetMapping("/")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Hello from Railway + Spring!");
    }

    @CrossOrigin(origins = {"http://localhost:3000", "https://tienda-online-blue.vercel.app"})
    @GetMapping("/products")
    @Operation(
            operationId = "Obtener productos",
            description = "Operacion de lectura",
            summary = "Se devuelve una lista de todos los productos almacenados en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    public ResponseEntity<ProductsResponse> getProducts(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "name", description = "Nombre del producto.", example = "Lapto Microsoft", required = false)
            @RequestParam(required = false) String name,
            @Parameter(name = "company", description = "Compañia del producto.", example = "Microsoft", required = false)
            @RequestParam(required = false) String company,
            @Parameter(name = "price", description = "Valor del producto. No tiene por que ser exacta", example = "100000", required = false)
            @RequestParam(required = false, defaultValue = "-1") Integer price,
            @Parameter(name = "description", description = "Descripción del producto.", example = "PC portatil", required = false)
            @RequestParam(required = false) String description,
            @Parameter(name = "urlImage", description = "Ruta de la imagen del producto.", example = "https://www.bing.com/images/search?view=detailV2&ccid=xHZ0d6sS&id=12F47E1B5CB1A4A032023FF57552C6AAA1B2CC50&thid=OIP.xHZ0d6sS_wJ1Sz3a4sJXtwHaB_&mediaurl=https%3a%2f%2fdc722jrlp2zu8.cloudfront.net%2fmedia%2fuploads%2f2018%2f07%2f30%2fcomo-funciona-elk.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.c4767477ab12ff02754b3ddae2c257b7%3frik%3dUMyyoarGUnX1Pw%26pid%3dImgRaw%26r%3d0&exph=201&expw=748&q=qu%c3%a9+es+elasticsearch&simid=608013738803674117&FORM=IRPRST&ck=629485999070956A124FAF753729FF17&selectedIndex=2&itb=0&ajaxhist=0&ajaxserp=0", required = false)
            @RequestParam(required = false) String urlImage,
            @Parameter(name = "largeDescription", description = "descripción corta del producto. true o false", example = "Colombiano", required = false)
            @RequestParam(required = false) String largeDescription,
            @Parameter(name = "score", description = "Stock del producto. true o false", example = "5", required = false)
            @RequestParam(required = false, defaultValue = "-1") Integer score,
            @RequestParam(required = false, defaultValue = "false") Boolean aggregate){

        log.info("headers: {}", headers);
        ProductsResponse products = ProductService.getProducts(name, company, price, description, urlImage, largeDescription,  score, aggregate);
        return ResponseEntity.ok(products);
    }
    @CrossOrigin(origins = {"http://localhost:3000", "https://tienda-online-blue.vercel.app"})
    @GetMapping("/products/{id}")
    @Operation(
            operationId = "Obtener un producto",
            description = "Operacion de lectura",
            summary = "Se devuelve un producto a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Product> getProduct(@PathVariable String id){

        log.info("Solicitud recibida para buscar el producto {}", id);
        Product productModel = ProductService.getProduct(id);
        if (productModel != null) {
            return ResponseEntity.ok(productModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @CrossOrigin(origins = {"http://localhost:3000", "https://tienda-online-blue.vercel.app"})
    @DeleteMapping("/products/{id}")
    @Operation(
            operationId = "Eliminar un producto",
            description = "Operacion de escritura",
            summary = "Se elimina un producto a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Void> delProduct(@PathVariable String id) {

        log.info("Solicitud recibida para eliminar el producto {}", id);
        boolean delProduct = ProductService.removeProduct(id);
        if (Boolean.TRUE.equals(delProduct)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @CrossOrigin(origins = {"http://localhost:3000", "https://tienda-online-blue.vercel.app"})
    @PostMapping("/products")
    @Operation(
            operationId = "Crear un producto",
            description = "Operacion de escritura",
            summary = "Se Crea un producto a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))))
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos incorrectos ingresados.")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest productRequest){

        log.info("Solicitud recibida para crear el producto {}", productRequest.getName());
        Product createdProduct = ProductService.createProduct(productRequest);

        if (createdProduct != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin(origins = {"http://localhost:3000", "https://tienda-online-blue.vercel.app"})
    @PutMapping("/products/{id}")
    @Operation(
            operationId = "Actualizar un producto",
            description = "Operacion de escritura",
            summary = "Se actualiza un producto a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a actualizar.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos incorrectos ingresados.")
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,@RequestBody ProductRequest productRequest){
        try {

            log.info("Solicitud recibida para actualizar el producto {}", productRequest.getName());

            Product prev = ProductService.getProduct(id);
            if (prev == null) {
                return ResponseEntity.notFound().build();
            } else {
                Product updatedProduct = ProductService.updateProduct(id,productRequest);
                return updatedProduct == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(updatedProduct);
            }

        } catch (Exception e) {
            log.error("Ocurrió un error inesperado al actualizar el producto {}", e.getMessage(), e);
            // Logamos error pero no lo devolvemos, para prevenir que un cliente sepa como
            // esta organizado nuestro codigo
            return ResponseEntity.internalServerError().build();
        }

    }
}