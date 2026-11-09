import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { UserAdminService } from './user-admin.service';
import { UserModel } from '../../../shared/models/user.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { environment } from '../../../../environments/environment';

interface RoleOption { id: string; name: string; }
interface RegionOption { id: string; name: string; }

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {

  users = signal<UserModel[]>([]);
  roles = signal<RoleOption[]>([]);
  regions = signal<RegionOption[]>([]);
  showForm = signal(false);

  newUser = {
    email: '',
    fullName: '',
    temporaryPassword: '',
    roleId: '',
    regionId: null as string | null
  };

  constructor(private userAdminService: UserAdminService, private http: HttpClient) {}

  ngOnInit(): void {
    this.refresh();
    this.http.get<RoleOption[]>(`${environment.apiUrl}/roles`).subscribe(data => this.roles.set(data));
    this.http.get<RegionOption[]>(`${environment.apiUrl}/regions`).subscribe(data => this.regions.set(data));
  }

  refresh(): void {
    this.userAdminService.listUsers().subscribe(data => this.users.set(data));
  }

  toggleForm(): void {
    this.showForm.update(v => !v);
  }

  submitNewUser(): void {
    this.userAdminService.createUser(this.newUser).subscribe(() => {
      this.showForm.set(false);
      this.newUser = { email: '', fullName: '', temporaryPassword: '', roleId: '', regionId: null };
      this.refresh();
    });
  }

  toggleActive(user: UserModel): void {
    this.userAdminService.setActive(user.id, !user.isActive).subscribe(() => this.refresh());
  }
}