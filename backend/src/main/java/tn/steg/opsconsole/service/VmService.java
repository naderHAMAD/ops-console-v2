package tn.steg.opsconsole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.steg.opsconsole.domain.Region;
import tn.steg.opsconsole.domain.User;
import tn.steg.opsconsole.domain.VirtualMachine;
import tn.steg.opsconsole.domain.VmJob;
import tn.steg.opsconsole.dto.request.CreateVmRequest;
import tn.steg.opsconsole.dto.response.DeployVmResponse;
import tn.steg.opsconsole.dto.response.VmDto;
import tn.steg.opsconsole.repository.RegionRepository;
import tn.steg.opsconsole.repository.UserRepository;
import tn.steg.opsconsole.repository.VmJobRepository;
import tn.steg.opsconsole.repository.VmRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VmService {

    private final VmRepository vmRepository;
    private final VmJobRepository vmJobRepository;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final AnsibleRunnerService ansibleRunnerService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<VmDto> listAll() {
        return vmRepository.findAll().stream().map(this::toDto).toList();
    }

    public DeployVmResponse createVm(CreateVmRequest request, Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Utilisateur courant introuvable"));
        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new IllegalArgumentException("Région introuvable"));

        VirtualMachine vm = VirtualMachine.builder()
                .name(request.name())
                .cpu(request.cpu())
                .ramGb(request.ramGb())
                .diskGb(request.diskGb())
                .diskType(request.diskType())
                .template(request.template())
                .network(request.network())
                .environment(request.environment())
                .backupEnabled(request.backupEnabled())
                .description(request.description())
                .status("pending")
                .region(region)
                .build();
        vm = vmRepository.save(vm);

        VmJob job = VmJob.builder()
                .vm(vm)
                .triggeredBy(currentUser)
                .status("pending")
                .build();
        job = vmJobRepository.save(job);

        String ansibleOutput;
        try {
            ansibleOutput = ansibleRunnerService.runPlaybook("create_vm_local_test.yml", java.util.Map.of(
                    "vm_name", vm.getName(),
                    "cpu", vm.getCpu(),
                    "ram_gb", vm.getRamGb(),
                    "disk_gb", vm.getDiskGb(),
                    "disk_type", vm.getDiskType(),
                    "network", vm.getNetwork() != null ? vm.getNetwork() : "default",
                    "environment", vm.getEnvironment()
            ));
        } catch (Exception e) {
            throw new IllegalStateException("Échec de l'exécution Ansible : " + e.getMessage());
        }

        auditService.log(currentUser, "VM_CREATE_REQUESTED", "VirtualMachine", vm.getId(), "Ansible exécuté");

        return new DeployVmResponse(job.getId(), "/tmp/vm-" + vm.getName() + "/info.txt", ansibleOutput);
    }

    private VmDto toDto(VirtualMachine vm) {
        return new VmDto(
                vm.getId(), vm.getName(), vm.getCpu(), vm.getRamGb(), vm.getDiskGb(),
                vm.getTemplate(), vm.getStatus(), vm.getRegion().getName(), vm.getCreatedAt()
        );
    }
}