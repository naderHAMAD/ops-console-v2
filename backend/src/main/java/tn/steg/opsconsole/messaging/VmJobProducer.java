package tn.steg.opsconsole.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tn.steg.opsconsole.config.RabbitMQConfig;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VmJobProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(UUID jobId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.VM_ROUTING_KEY,
                new JobMessage(jobId)
        );
    }
}
