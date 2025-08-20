package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession(
        maxInactiveIntervalInSeconds = 1800,       //
        sessionMapName              = "spring:session:sessions"
)
public class SessionConfig { }
