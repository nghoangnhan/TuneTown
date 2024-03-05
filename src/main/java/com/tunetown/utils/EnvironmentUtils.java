package com.tunetown.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@PropertySource("classpath:application.properties")
public class EnvironmentUtils implements EnvironmentAware {
    private static Environment env;

    public static String getEnvironmentValue(String propertyKey) {
        return env.getProperty(propertyKey);
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public static Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

    static {
        System.setProperty("database-url", dotenv().get("database-url"));
        System.setProperty("database-username", dotenv().get("database-username"));
        System.setProperty("database-password", dotenv().get("database-password"));
    }
}
