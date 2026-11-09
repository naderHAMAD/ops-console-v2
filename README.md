# Ops Console — JBoss & VM (STEG)

Squelette complet : backend Spring Boot + frontend Angular + orchestration Ansible/AWX.

## Démarrage rapide

```bash
# 1. Infrastructure (DB, file de messages)
docker compose up -d postgres pgadmin rabbitmq

# 2. Backend (dev local, hors Docker pour itérer plus vite)
cd backend
mvn spring-boot:run
# API disponible sur http://localhost:8080/api

# 3. Frontend
cd frontend
npm install
npm start
# App disponible sur http://localhost:4200
```



## AWX (Ansible Automation Platform)

Non inclus dans `docker-compose.yml` (installation officielle séparée via AWX Operator
ou le dépôt awx-on-docker : https://github.com/ansible/awx/blob/devel/INSTALL.md).

Une fois AWX opérationnel :
1. Crée deux job_templates : un pour la mise à jour JBoss, un pour la création de VM
2. Configure leurs credentials (SSH, Vault) et inventaires (Nord/Sud/National)
3. Renseigne dans le backend (variables d'environnement ou `application.yml`) :
   - `AWX_BASE_URL`
   - `AWX_TOKEN`
   - `AWX_JT_UPDATE_JBOSS` (ID numérique du job_template)
   - `AWX_JT_CREATE_VM` (ID numérique du job_template)

## Structure

- `backend/` — API Spring Boot (JWT + 2FA, RBAC, RabbitMQ, WebSocket, client AWX)
- `frontend/` — Angular 18 standalone (dashboard, update JBoss, VM, gestion utilisateurs)
- `docker-compose.yml` — Postgres, pgAdmin, RabbitMQ, backend, frontend

## Comptes par défaut

Un compte Super Admin est créé par la migration Flyway V2 :
- Email : `admin@steg.tn`
- Mot de passe : `ChangeMe123!` — **à changer immédiatement après la première connexion**

## Prochaines étapes suggérées

- Écrire les playbooks Ansible réels (`update_jboss.yml`, `create_vm.yml`) et les
  enregistrer comme job_templates dans AWX
- Ajouter des tests d'intégration (Testcontainers pour le backend, Cypress pour le frontend)
- Remplacer le polling simplifié d'AWX (`UpdateJobConsumer`/`VmJobConsumer`) par les
  notification templates AWX (webhook vers le backend) pour éviter le polling actif
- Ajouter la génération/scan du QR code TOTP lors de l'activation du 2FA
