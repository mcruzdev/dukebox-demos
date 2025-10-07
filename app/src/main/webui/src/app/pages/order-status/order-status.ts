import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Button } from 'primeng/button';
import { Step, StepList, StepPanel, StepPanels, Stepper } from 'primeng/stepper';
import { WebsocketService } from '../../web-socket-service';
import { OrderService } from '../../order-service';

@Component({
  selector: 'app-order-status',
  imports: [Stepper, Step, StepList, StepPanels, StepPanel, Button],
  templateUrl: './order-status.html',
  styleUrl: './order-status.scss'
})
export class OrderStatus implements OnInit {

  route = inject(ActivatedRoute);
  webSocketService = inject(WebsocketService);
  orderService = inject(OrderService);
  status = signal(1);

  statuses: any = {
    "RECEIVED": 1,
    "PREPARED": 2,
    "IN_TRANSIT": 3,
    "DELIVERED": 4
  }

  ngOnInit(): void {

    this.route.params.subscribe(({ id }) => {
      this.webSocketService.receiveMessages().subscribe(msg => {
        this.status.set(this.statuses[JSON.parse(msg).status]);
      });

      this.orderService.findById(id).subscribe(response => {
        this.status.set(this.statuses[response.status])
      });
    });



  }


}
