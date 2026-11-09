import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { ServerModel, TriggerUpdateRequest } from '../../../shared/models/server.model';

@Injectable({ providedIn: 'root' })
export class UpdateJobService {
  constructor(private http: HttpClient) {}

  getServers() {
    return this.http.get<ServerModel[]>(`${environment.apiUrl}/servers`);
  }

  triggerUpdate(payload: TriggerUpdateRequest) {
    return this.http.post<any[]>(`${environment.apiUrl}/jobs/update`, payload);
  }

  triggerRollback(payload: { serverIds: string[] }) {
    return this.http.post<any[]>(`${environment.apiUrl}/jobs/update/rollback`, payload);
  }
}
