package tn.steg.opsconsole.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class AnsibleRunnerService {

    // Chemin Windows de la racine du projet (les \ sont doubles car c'est une chaine Java)
    private static final String PROJECT_ROOT_WINDOWS = "D:\\Users\\73554\\Downloads\\ops-console-main\\ops-console-main";
    private static final String PROJECT_ROOT_WSL = "/mnt/d/Users/73554/Downloads/ops-console-main/ops-console-main";

    // Distribution WSL a utiliser (verifie avec : wsl -l -v)
    private static final String WSL_DISTRO = "Ubuntu";

    private static final long TIMEOUT_MINUTES = 30;
    private static final Pattern SAFE_PLAYBOOK = Pattern.compile("[A-Za-z0-9_.-]+\\.ya?ml");
    private static final Pattern SAFE_REL_PATH = Pattern.compile("[A-Za-z0-9_./-]+");

    private final ObjectMapper mapper = new ObjectMapper();

    /** Execute un playbook via WSL et retourne la sortie complete (stdout + stderr). */
    public String runPlaybook(String playbookName, Map<String, Object> extraVars) throws Exception {
        checkPlaybookName(playbookName);
        return runAnsible(List.of(
                "playbooks/" + playbookName,
                "-e", toJson(extraVars)));
    }

    /**
     * Ecrit un playbook et un fichier de variables recus dynamiquement sur disque,
     * puis execute ansible-playbook dessus via WSL.
     * ATTENTION : execute du YAML fourni par l'utilisateur, a reserver au Super Admin.
     */
    public String runUploadedPlaybook(String playbookContent, String varsJson) throws Exception {
        if (playbookContent == null || playbookContent.isBlank()) {
            throw new IllegalArgumentException("Le contenu du playbook est vide");
        }
        if (varsJson == null || varsJson.isBlank()) {
            varsJson = "{}";
        }
        mapper.readTree(varsJson); // verifie que c'est du JSON valide

        String id = UUID.randomUUID().toString();

        Path playbookDir = Paths.get(PROJECT_ROOT_WINDOWS, "ansible", "playbooks", "uploaded");
        Path varsDir = Paths.get(PROJECT_ROOT_WINDOWS, "ansible", "vars");
        Files.createDirectories(playbookDir);
        Files.createDirectories(varsDir);

        Path playbookFile = playbookDir.resolve("uploaded-" + id + ".yml");
        Path varsFile = varsDir.resolve("vm-" + id + ".json");

        try {
            Files.writeString(playbookFile, playbookContent, StandardCharsets.UTF_8);
            Files.writeString(varsFile, varsJson, StandardCharsets.UTF_8);

            return runAnsible(List.of(
                    "playbooks/uploaded/uploaded-" + id + ".yml",
                    "-e", "@vars/vm-" + id + ".json"));
        } finally {
            // Fichiers potentiellement sensibles : supprimes apres execution
            try { Files.deleteIfExists(playbookFile); } catch (IOException ignored) { }
            try { Files.deleteIfExists(varsFile); } catch (IOException ignored) { }
        }
    }

    /** Exécute un playbook avec un inventaire réel et des extra_vars, retourne la sortie complète. */
    public String runPlaybookWithInventory(String playbookName, String inventoryRelativePath, Map<String, Object> extraVars) throws Exception {
        checkPlaybookName(playbookName);

        String id = UUID.randomUUID().toString();
        Path varsDir = Paths.get(PROJECT_ROOT_WINDOWS, "ansible", "vars");
        Files.createDirectories(varsDir);
        Path varsFile = varsDir.resolve("update-" + id + ".json");

        try {
            Files.writeString(varsFile, toJson(extraVars), StandardCharsets.UTF_8);
            return runAnsible(List.of(
                    "-i", inventoryRelativePath,
                    "playbooks/" + playbookName,
                    "-e", "@vars/update-" + id + ".json"));
        } finally {
            try { Files.deleteIfExists(varsFile); } catch (IOException ignored) { }
        }
    }

    
    // ------------------------------------------------------------------
    // Interne
    // ------------------------------------------------------------------

    /** Lance ansible-playbook dans WSL, sans shell. Ajoute le code de retour en fin de sortie si echec. */
    private String runAnsible(List<String> ansibleArgs) throws Exception {
        List<String> cmd = new ArrayList<>(List.of(
                "wsl.exe", "-d", WSL_DISTRO,
                "--cd", PROJECT_ROOT_WSL + "/ansible",
                "--exec", "env",
                "ANSIBLE_ROLES_PATH=roles",
                "ANSIBLE_FORCE_COLOR=0"));

        // /mnt/d est "world writable" : Ansible ignore ansible.cfg, on le force s'il existe
        if (Files.exists(Paths.get(PROJECT_ROOT_WINDOWS, "ansible", "ansible.cfg"))) {
            cmd.add("ANSIBLE_CONFIG=ansible.cfg");
        }
        cmd.add("ansible-playbook");
        cmd.addAll(ansibleArgs);

        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();

        CompletableFuture<String> output = CompletableFuture.supplyAsync(() -> {
            try (var in = process.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                return "[lecture de la sortie impossible : " + e + "]";
            }
        });

        if (!process.waitFor(TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            return output.getNow("") + "\n[TIMEOUT apres " + TIMEOUT_MINUTES + " min]\n";
        }

        String out = output.get(10, TimeUnit.SECONDS);
        int exit = process.exitValue();
        if (exit != 0) {
            out += "\n[ansible-playbook a echoue : code de retour " + exit + "]\n";
        }
        return out;
    }

    private String toJson(Map<String, Object> extraVars) throws IOException {
        return mapper.writeValueAsString(extraVars == null ? Map.of() : extraVars);
    }

    private static void checkPlaybookName(String name) {
        if (name == null || !SAFE_PLAYBOOK.matcher(name).matches()) {
            throw new IllegalArgumentException("Nom de playbook invalide : " + name);
        }
    }

    private static void checkRelativePath(String path) {
        if (path == null || !SAFE_REL_PATH.matcher(path).matches()
                || path.contains("..") || path.startsWith("/")) {
            throw new IllegalArgumentException("Chemin invalide : " + path);
        }
    }
}