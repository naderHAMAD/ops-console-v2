import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ServerModel } from '../../shared/models/server.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  getServers() {
    return this.http.get<ServerModel[]>(`${environment.apiUrl}/servers`);
  }

  getRecentAudit() {
    return this.http.get<any[]>(`${environment.apiUrl}/audit`);
  }
}
