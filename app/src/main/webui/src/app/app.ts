import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Toolbar } from 'primeng/toolbar';
import { Image } from 'primeng/image';
import { Button } from 'primeng/button';
import { OrderService } from './order-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toolbar, Image, Button],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  protected readonly title = signal('dukebox');
  orderService = inject(OrderService);

  seeOrders() {
    this.orderService.http.get("http://localhost:8080/api/orders", {
      headers: {
        "x-User-Id": localStorage.getItem("dukebox:::userId") as string
      }
    }).subscribe(res => console.log(res));
  }
}
