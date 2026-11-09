import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JobStatus } from '../../shared/models/server.model';

@Injectable({ providedIn: 'root' })
export class JobSocketService {

  private client: Client | null = null;

  private connect(): Promise<void> {
    if (this.client?.connected) return Promise.resolve();

    return new Promise((resolve) => {
      this.client = new Client({
        webSocketFactory: () => new SockJS(environment.wsUrl),
        reconnectDelay: 3000
      });
      this.client.onConnect = () => resolve();
      this.client.activate();
    });
  }

  /** S'abonne aux mises à jour d'un job précis (update JBoss ou VM). */
  watchJob(jobId: string): Observable<JobStatus> {
    return new Observable<JobStatus>(subscriber => {
      this.connect().then(() => {
        const sub = this.client!.subscribe(`/topic/jobs/${jobId}`, (message: IMessage) => {
          subscriber.next(JSON.parse(message.body) as JobStatus);
        });
        return () => sub.unsubscribe();
      });
    });
  }

  disconnect(): void {
    this.client?.deactivate();
  }
}
