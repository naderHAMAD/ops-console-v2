import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { CreateUserRequest, UserModel } from '../../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class UserAdminService {
  constructor(private http: HttpClient) {}

  listUsers() {
    return this.http.get<UserModel[]>(`${environment.apiUrl}/users`);
  }

  createUser(payload: CreateUserRequest) {
    return this.http.post<UserModel>(`${environment.apiUrl}/users`, payload);
  }

  setActive(userId: string, active: boolean) {
    return this.http.patch<UserModel>(`${environment.apiUrl}/users/${userId}/active?active=${active}`, {});
  }
}
