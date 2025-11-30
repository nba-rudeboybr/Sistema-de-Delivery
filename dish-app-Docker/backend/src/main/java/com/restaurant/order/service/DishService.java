package com.restaurant.order.service;

import com.restaurant.order.model.Dish;
import com.restaurant.order.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DishService {
    
    @Autowired
    private DishRepository dishRepository;
    
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }
    
    public Optional<Dish> getDishById(Long id) {
        return dishRepository.findById(id);
    }
    
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }
    
    public Dish updateDish(Long id, Dish dishDetails) {
        Optional<Dish> optionalDish = dishRepository.findById(id);
        if (optionalDish.isPresent()) {
            Dish dish = optionalDish.get();
            dish.setName(dishDetails.getName());
            dish.setDescription(dishDetails.getDescription());
            dish.setPrice(dishDetails.getPrice());
            return dishRepository.save(dish);
        }
        throw new RuntimeException("Dish not found with id: " + id);
    }
    
    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }
    
    public void initializeDefaultDishes() {
        if (dishRepository.count() == 0) {
            // Criar pratos padrão se não existirem
            Dish pizza = new Dish("Pizza Margherita", "Pizza com molho de tomate, mussarela e manjericão", 25.90);
            Dish hamburger = new Dish("Hambúrguer Clássico", "Hambúrguer com carne, alface, tomate e queijo", 18.50);
            Dish salad = new Dish("Salada Caesar", "Salada com alface, croutons, queijo parmesão e molho caesar", 15.90);
            Dish fries = new Dish("Batata Frita", "Batata frita crocante", 8.90);
            Dish soda = new Dish("Refrigerante", "Refrigerante gelado", 4.50);
            
            dishRepository.save(pizza);
            dishRepository.save(hamburger);
            dishRepository.save(salad);
            dishRepository.save(fries);
            dishRepository.save(soda);
            
            System.out.println("Pratos padrão inicializados com sucesso!");
        }
    }
}
