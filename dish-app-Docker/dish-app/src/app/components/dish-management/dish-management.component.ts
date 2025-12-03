// src/app/components/dish-management/dish-management.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-dish-management',
  templateUrl: './dish-management.component.html',
  styleUrls: ['./dish-management.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class DishManagementComponent implements OnInit {
  dishes: any[] = [];
  newDish = {
    name: '',
    description: '',
    price: 0
  };
  showNewDishForm = false;
  editingDish: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDishes();
  }

  loadDishes(): void {
    this.http.get<any[]>('http://localhost:8080/dishes').subscribe(dishes => {
      this.dishes = dishes;
    });
  }

  createNewDish(): void {
    this.showNewDishForm = true;
    this.newDish = {
      name: '',
      description: '',
      price: 0
    };
    this.editingDish = null;
  }

  editDish(dish: any): void {
    this.editingDish = dish;
    this.newDish = {
      name: dish.name,
      description: dish.description,
      price: dish.price
    };
    this.showNewDishForm = true;
  }

  saveDish(): void {
    if (this.editingDish) {
      // Atualizar prato existente
      this.http.put(`http://localhost:8080/dishes/${this.editingDish.id}`, this.newDish).subscribe(() => {
        this.loadDishes();
        this.showNewDishForm = false;
        this.editingDish = null;
        this.newDish = {
          name: '',
          description: '',
          price: 0
        };
      });
    } else {
      // Criar novo prato
      this.http.post('http://localhost:8080/dishes', this.newDish).subscribe(() => {
        this.loadDishes();
        this.showNewDishForm = false;
        this.newDish = {
          name: '',
          description: '',
          price: 0
        };
      });
    }
  }

  deleteDish(dish: any): void {
    if (confirm(`Tem certeza que deseja excluir o prato "${dish.name}"?`)) {
      this.http.delete(`http://localhost:8080/dishes/${dish.id}`).subscribe(() => {
        this.loadDishes();
      });
    }
  }

  cancelEdit(): void {
    this.showNewDishForm = false;
    this.editingDish = null;
    this.newDish = {
      name: '',
      description: '',
      price: 0
    };
  }
}
