package tn.steg.opsconsole.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "ops-console.jobs";
    public static final String UPDATE_QUEUE = "ops-console.update-jobs";
    public static final String VM_QUEUE = "ops-console.vm-jobs";
    public static final String UPDATE_ROUTING_KEY = "job.update";
    public static final String VM_ROUTING_KEY = "job.vm";

    @Bean
    public TopicExchange jobsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue updateJobQueue() {
        return QueueBuilder.durable(UPDATE_QUEUE).build();
    }

    @Bean
    public Queue vmJobQueue() {
        return QueueBuilder.durable(VM_QUEUE).build();
    }

    @Bean
    public Binding updateBinding(Queue updateJobQueue, TopicExchange jobsExchange) {
        return BindingBuilder.bind(updateJobQueue).to(jobsExchange).with(UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding vmBinding(Queue vmJobQueue, TopicExchange jobsExchange) {
        return BindingBuilder.bind(vmJobQueue).to(jobsExchange).with(VM_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
