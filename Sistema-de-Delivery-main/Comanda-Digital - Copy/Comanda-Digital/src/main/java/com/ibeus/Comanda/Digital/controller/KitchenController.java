package com.ibeus.Comanda.Digital.controller;

import com.ibeus.Comanda.Digital.model.KitchenOrder;
import com.ibeus.Comanda.Digital.model.KitchenOrderItem;
import com.ibeus.Comanda.Digital.service.KitchenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/kitchen")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Kitchen", description = "API para gerenciamento da cozinha")
public class KitchenController {
    
    @Autowired
    private KitchenService kitchenService;
    
    @Operation(summary = "Listar todos os pedidos da cozinha", description = "Retorna uma lista com todos os pedidos da cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos da cozinha retornada com sucesso")
    })
    @GetMapping("/orders")
    public List<KitchenOrder> getAllKitchenOrders() {
        return kitchenService.getAllKitchenOrders();
    }
    
    @Operation(summary = "Listar pedidos ativos da cozinha", description = "Retorna uma lista com todos os pedidos ativos da cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos ativos retornada com sucesso")
    })
    @GetMapping("/orders/active")
    public List<KitchenOrder> getActiveKitchenOrders() {
        return kitchenService.getActiveKitchenOrders();
    }
    
    @Operation(summary = "Listar novos pedidos", description = "Retorna uma lista com todos os novos pedidos na cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de novos pedidos retornada com sucesso")
    })
    @GetMapping("/orders/new")
    public List<KitchenOrder> getNewOrders() {
        return kitchenService.getNewOrders();
    }
    
    @Operation(summary = "Listar pedidos em preparo", description = "Retorna uma lista com todos os pedidos em preparo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos em preparo retornada com sucesso")
    })
    @GetMapping("/orders/preparing")
    public List<KitchenOrder> getPreparingOrders() {
        return kitchenService.getPreparingOrders();
    }
    
    @Operation(summary = "Listar pedidos prontos", description = "Retorna uma lista com todos os pedidos prontos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos prontos retornada com sucesso")
    })
    @GetMapping("/orders/ready")
    public List<KitchenOrder> getReadyOrders() {
        return kitchenService.getReadyOrders();
    }
    
    @Operation(summary = "Buscar pedidos por mesa", description = "Retorna uma lista de pedidos da cozinha de uma mesa específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos da mesa retornada com sucesso")
    })
    @GetMapping("/orders/table/{tableNumber}")
    public List<KitchenOrder> getOrdersByTable(@Parameter(description = "Número da mesa") @PathVariable Integer tableNumber) {
        return kitchenService.getOrdersByTable(tableNumber);
    }
    
    @Operation(summary = "Buscar pedido da cozinha por ID", description = "Retorna um pedido específico da cozinha baseado no ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido da cozinha encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido da cozinha não encontrado")
    })
    @GetMapping("/orders/{id}")
    public ResponseEntity<KitchenOrder> getKitchenOrderById(@Parameter(description = "ID do pedido da cozinha") @PathVariable Long id) {
        Optional<KitchenOrder> order = kitchenService.getKitchenOrderById(id);
        return order.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Atualizar status do pedido da cozinha", description = "Atualiza o status de um pedido na cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido da cozinha não encontrado")
    })
    @PatchMapping("/orders/{id}/status")
    public KitchenOrder updateOrderStatus(@Parameter(description = "ID do pedido da cozinha") @PathVariable Long id, 
                                        @Parameter(description = "Novo status do pedido") @RequestBody KitchenOrderStatusRequest request) {
        return kitchenService.updateOrderStatus(id, request.getStatus());
    }
    
    @Operation(summary = "Atualizar status de um item", description = "Atualiza o status de preparo de um item específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status do item atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido ou item não encontrado")
    })
    @PatchMapping("/orders/{orderId}/items/{itemId}/status")
    public KitchenOrder updateItemStatus(@Parameter(description = "ID do pedido da cozinha") @PathVariable Long orderId, 
                                       @Parameter(description = "ID do item") @PathVariable Long itemId,
                                       @Parameter(description = "Novo status do item") @RequestBody ItemStatusRequest request) {
        return kitchenService.updateItemStatus(orderId, itemId, request.getStatus());
    }
    
    @Operation(summary = "Atualizar prioridade do pedido", description = "Atualiza a prioridade de um pedido na cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prioridade do pedido atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido da cozinha não encontrado")
    })
    @PatchMapping("/orders/{id}/priority")
    public KitchenOrder updateOrderPriority(@Parameter(description = "ID do pedido da cozinha") @PathVariable Long id, 
                                          @Parameter(description = "Nova prioridade (1=normal, 2=alta, 3=urgente)") @RequestBody PriorityRequest request) {
        return kitchenService.updateOrderPriority(id, request.getPriority());
    }
    
    @Operation(summary = "Excluir pedido da cozinha", description = "Remove um pedido da cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pedido da cozinha excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido da cozinha não encontrado")
    })
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteKitchenOrder(@Parameter(description = "ID do pedido da cozinha a ser excluído") @PathVariable Long id) {
        kitchenService.deleteKitchenOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Obter estatísticas da cozinha", description = "Retorna estatísticas dos pedidos na cozinha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    @GetMapping("/stats")
    public KitchenStats getKitchenStats() {
        return new KitchenStats(
            kitchenService.countActiveOrders(),
            kitchenService.countNewOrders(),
            kitchenService.countPreparingOrders(),
            kitchenService.countReadyOrders()
        );
    }
    
    // Classes auxiliares para requisições
    public static class KitchenOrderStatusRequest {
        private KitchenOrder.KitchenOrderStatus status;
        
        public KitchenOrder.KitchenOrderStatus getStatus() {
            return status;
        }
        
        public void setStatus(KitchenOrder.KitchenOrderStatus status) {
            this.status = status;
        }
    }
    
    public static class ItemStatusRequest {
        private KitchenOrderItem.PreparationStatus status;
        
        public KitchenOrderItem.PreparationStatus getStatus() {
            return status;
        }
        
        public void setStatus(KitchenOrderItem.PreparationStatus status) {
            this.status = status;
        }
    }
    
    public static class PriorityRequest {
        private Integer priority;
        
        public Integer getPriority() {
            return priority;
        }
        
        public void setPriority(Integer priority) {
            this.priority = priority;
        }
    }
    
    public static class KitchenStats {
        private long activeOrders;
        private long newOrders;
        private long preparingOrders;
        private long readyOrders;
        
        public KitchenStats(long activeOrders, long newOrders, long preparingOrders, long readyOrders) {
            this.activeOrders = activeOrders;
            this.newOrders = newOrders;
            this.preparingOrders = preparingOrders;
            this.readyOrders = readyOrders;
        }
        
        // Getters
        public long getActiveOrders() { return activeOrders; }
        public long getNewOrders() { return newOrders; }
        public long getPreparingOrders() { return preparingOrders; }
        public long getReadyOrders() { return readyOrders; }
    }
}

