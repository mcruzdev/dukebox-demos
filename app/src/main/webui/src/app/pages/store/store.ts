import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Image } from 'primeng/image';
import { OrderService } from '../../order-service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs';

@Component({
  selector: 'app-store',
  imports: [Image, Card, CurrencyPipe, Button],
  templateUrl: './store.html',
  styleUrl: './store.scss'
})
export class Store {
  orderService = inject(OrderService);
  router = inject(Router)

  readonly gifts = signal([
    {
      id: 1,
      name: 'Quarkus Stickers',
      price: 19.99,
      image: 'quarkus.svg'
    },
    {
      id: 2,
      name: 'Dapr Swag',
      price: 19.00,
      image: 'dapr.png'
    },
    {
      id: 3,
      name: 'Java Stickers',
      price: 20.00,
      image: 'java.svg'
    },
    {
      id: 5,
      name: 'Kubernetes Swag',
      price: 30.00,
      image: 'kubernetes.svg'
    }
  ]);

  buy(gift: any) {
    console.log('buying gift: ', gift);
    this.orderService.sendOrder({
      items: [gift]
    })
      .subscribe((response) => {
        this.router.navigate(['/order-status', response.id])
      });
  }
}
