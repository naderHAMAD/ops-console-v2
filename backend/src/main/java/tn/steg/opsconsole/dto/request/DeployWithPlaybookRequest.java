package tn.steg.opsconsole.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeployWithPlaybookRequest(
        @NotBlank String playbookContent,   // contenu texte du fichier .yml choisi par l'utilisateur
        @NotBlank String vmSpecJson         // le JSON généré par le wizard (déjà sous forme de texte)
) {}