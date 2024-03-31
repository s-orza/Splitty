package server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Spring uses stomp (a subprotocol of websocket) so need to have an endpoint that can be requested
     * by the client to start a websocket.
     * @param registry to configure the endpoint
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket");
    }

    /**
     * After having a websocket this configures the message program. This method enables the message broker
     * And has an application prefix. this means that if your message is sent to /app it first processes the message.
     * It can still go to the broker but doesn't have to.
     * And if it is /app then it will directly go to the message broker (every message in specific place will
     * be automatically delivered to you.
     * @param config to config the messageBroker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
