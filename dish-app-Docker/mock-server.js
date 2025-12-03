const express = require('express');
const cors = require('cors');
const app = express();
const port = 8080;

app.use(cors());
app.use(express.json());

// Dados mock para delivery
let orders = [
  {
    id: 1,
    customerName: "João Silva",
    customerPhone: "(11) 99999-9999",
    deliveryAddress: "Rua das Flores, 123 - Centro",
    status: "NEW",
    totalAmount: 45.90,
    deliveryFee: 5.00,
    items: [
      { dishId: 1, quantity: 1 },
      { dishId: 2, quantity: 1 }
    ],
    createdAt: new Date().toISOString()
  },
  {
    id: 2,
    customerName: "Maria Santos",
    customerPhone: "(11) 88888-8888",
    deliveryAddress: "Av. Paulista, 456 - Bela Vista",
    status: "PREPARING",
    totalAmount: 32.40,
    deliveryFee: 5.00,
    items: [
      { dishId: 1, quantity: 1 },
      { dishId: 3, quantity: 1 }
    ],
    createdAt: new Date(Date.now() - 15 * 60 * 1000).toISOString() // 15 minutos atrás
  },
  {
    id: 3,
    customerName: "Pedro Costa",
    customerPhone: "(11) 77777-7777",
    deliveryAddress: "Rua Augusta, 789 - Consolação",
    status: "READY",
    totalAmount: 18.50,
    deliveryFee: 5.00,
    items: [
      { dishId: 2, quantity: 1 }
    ],
    createdAt: new Date(Date.now() - 30 * 60 * 1000).toISOString() // 30 minutos atrás
  }
];

let dishes = [
  {
    id: 1,
    name: "Pizza Margherita",
    description: "Pizza com molho de tomate, mussarela e manjericão",
    price: 25.90
  },
  {
    id: 2,
    name: "Hambúrguer Clássico",
    description: "Hambúrguer com carne, alface, tomate e queijo",
    price: 18.50
  },
  {
    id: 3,
    name: "Salada Caesar",
    description: "Salada com alface, croutons, queijo parmesão e molho caesar",
    price: 15.90
  }
];

// Rotas para Comandas
app.get('/orders', (req, res) => {
  res.json(orders);
});

app.get('/orders/:id', (req, res) => {
  const order = orders.find(o => o.id == req.params.id);
  if (order) {
    res.json(order);
  } else {
    res.status(404).json({ message: 'Comanda não encontrada' });
  }
});

app.post('/orders', (req, res) => {
  const newOrder = {
    id: orders.length + 1,
    tableNumber: req.body.tableNumber,
    customerName: req.body.customerName || '',
    status: 'OPEN',
    totalAmount: 0,
    items: []
  };
  orders.push(newOrder);
  res.json(newOrder);
});

app.put('/orders/:id', (req, res) => {
  const orderIndex = orders.findIndex(o => o.id == req.params.id);
  if (orderIndex !== -1) {
    orders[orderIndex] = { ...orders[orderIndex], ...req.body };
    res.json(orders[orderIndex]);
  } else {
    res.status(404).json({ message: 'Comanda não encontrada' });
  }
});

app.delete('/orders/:id', (req, res) => {
  const orderIndex = orders.findIndex(o => o.id == req.params.id);
  if (orderIndex !== -1) {
    orders.splice(orderIndex, 1);
    res.json({ message: 'Comanda excluída com sucesso' });
  } else {
    res.status(404).json({ message: 'Comanda não encontrada' });
  }
});

// Rotas para Pratos
app.get('/dishes', (req, res) => {
  res.json(dishes);
});

app.get('/dishes/:id', (req, res) => {
  const dish = dishes.find(d => d.id == req.params.id);
  if (dish) {
    res.json(dish);
  } else {
    res.status(404).json({ message: 'Prato não encontrado' });
  }
});

app.post('/dishes', (req, res) => {
  const newDish = {
    id: dishes.length + 1,
    name: req.body.name,
    description: req.body.description,
    price: req.body.price
  };
  dishes.push(newDish);
  res.json(newDish);
});

app.put('/dishes/:id', (req, res) => {
  const dishIndex = dishes.findIndex(d => d.id == req.params.id);
  if (dishIndex !== -1) {
    dishes[dishIndex] = { ...dishes[dishIndex], ...req.body };
    res.json(dishes[dishIndex]);
  } else {
    res.status(404).json({ message: 'Prato não encontrado' });
  }
});

app.delete('/dishes/:id', (req, res) => {
  const dishIndex = dishes.findIndex(d => d.id == req.params.id);
  if (dishIndex !== -1) {
    dishes.splice(dishIndex, 1);
    res.json({ message: 'Prato excluído com sucesso' });
  } else {
    res.status(404).json({ message: 'Prato não encontrado' });
  }
});

app.listen(port, () => {
  console.log(`🚀 Servidor Mock rodando em http://localhost:${port}`);
  console.log(`📋 Comandas: http://localhost:${port}/orders`);
  console.log(`🍽️ Pratos: http://localhost:${port}/dishes`);
});
