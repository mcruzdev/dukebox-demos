import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  http = inject(HttpClient)

  sendOrder(order: any): Observable<{ id: string }> {
    return this.http.post<{ id: string }>(`/api/orders`, order);
  }

  findById(id: string): Observable<{ status: string }> {
    return this.http.get<{ status: string }>("/api/orders/" + id);
  }
}
