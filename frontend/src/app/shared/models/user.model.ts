export interface UserModel {
  id: string;
  email: string;
  fullName: string;
  isActive: boolean;
  is2faEnabled: boolean;
  roleName: string;
  regionName: string | null;
}

export interface CreateUserRequest {
  email: string;
  fullName: string;
  temporaryPassword: string;
  roleId: string;
  regionId: string | null;
}
