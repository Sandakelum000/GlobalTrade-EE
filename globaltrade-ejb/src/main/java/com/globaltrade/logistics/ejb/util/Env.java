package com.globaltrade.logistics.ejb.util;

import com.globaltrade.logistics.core.service.ConfigService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.InputStream;
import java.util.Properties;

@Startup
@Singleton
public class Env implements ConfigService {
    private Properties properties;

    @PostConstruct
    public void init(){
        properties = new Properties();
        try(InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("app.properties")){

            if (inputStream == null){
                throw new RuntimeException("app.properties not found");
            }
            properties.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("app.properties loading failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

}
