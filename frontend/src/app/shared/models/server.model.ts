export interface ServerModel {
  id: string;
  hostname: string;
  ipAddress: string | null;
  jbossVersion: string | null;
  status: 'up' | 'down' | 'unknown';
  lastUpdate: string | null;
  regionName: string;
}

export interface TriggerUpdateRequest {
  serverIds: string[];
  targetVersion: string;
}

export interface JobStatus {
  jobId: string;
  targetName: string | null;
  status: 'pending' | 'running' | 'success' | 'failed' | 'rolled_back';
  lastLogLine: string | null;
  startedAt: string | null;
  finishedAt: string | null;
}
