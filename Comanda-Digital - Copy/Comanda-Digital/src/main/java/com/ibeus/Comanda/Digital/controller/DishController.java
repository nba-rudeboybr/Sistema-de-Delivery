package com.ibeus.Comanda.Digital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ibeus.Comanda.Digital.model.Dish;
import com.ibeus.Comanda.Digital.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Dishes", description = "API para gerenciamento de pratos do cardápio")
public class DishController {

    @Autowired
    private DishService dishService;

    @Operation(summary = "Listar todos os pratos", description = "Retorna uma lista com todos os pratos cadastrados no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pratos retornada com sucesso")
    })
    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.findAll();
    }

    @Operation(summary = "Buscar prato por ID", description = "Retorna um prato específico baseado no ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prato encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @GetMapping("/{id}")
    public Dish getDishById(@Parameter(description = "ID do prato a ser buscado") @PathVariable Long id) {
        return dishService.findById(id);
    }

    @Operation(summary = "Criar novo prato", description = "Cadastra um novo prato no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prato criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public Dish createDish(@Parameter(description = "Dados do prato a ser criado") @RequestBody Dish dish) {
        return dishService.create(dish);
    }

    @Operation(summary = "Atualizar prato", description = "Atualiza os dados de um prato existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prato atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Prato não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping("/{id}")
    public Dish updateDish(@Parameter(description = "ID do prato a ser atualizado") @PathVariable Long id, 
                          @Parameter(description = "Novos dados do prato") @RequestBody Dish dish) {
        return dishService.update(id, dish);
    }

    @Operation(summary = "Excluir prato", description = "Remove um prato do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Prato excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Prato não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@Parameter(description = "ID do prato a ser excluído") @PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}