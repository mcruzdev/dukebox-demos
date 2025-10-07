import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private readonly webSocketSubject = new Subject<any>();

  constructor() {
    const webSocket = new WebSocket("ws://" + location.host + "/ws/default");

    webSocket.onopen = function () {
      console.log('ws connected');
    }

    webSocket.onmessage = (msg) => {
      console.log('onmessage: ', msg);
      this.webSocketSubject.next(msg.data);
    }

    webSocket.onclose = () => this.webSocketSubject.complete();
  }

  receiveMessages(): Observable<any> {
    return this.webSocketSubject;
  }
}
