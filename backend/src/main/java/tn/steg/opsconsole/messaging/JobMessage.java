package tn.steg.opsconsole.messaging;

import java.util.UUID;

/** Message léger : seul l'ID du job est transmis, le consumer relit l'état complet en DB. */
public record JobMessage(UUID jobId) {}
