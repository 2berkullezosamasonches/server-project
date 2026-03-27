package com.warehouse.warehouse_manager.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

@Component
public class KeyProvider {

    @Value("${signature.keystore.path}")
    private String keystorePath;

    @Value("${signature.keystore.password}")
    private String keystorePassword;

    @Value("${signature.key.alias}")
    private String keyAlias;

    @Value("${signature.key.password}")
    private String keyPassword;

    public PrivateKey getPrivateKey() throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        // Загружаем хранилище (оно должно лежать в корне проекта или в resources)
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, keystorePassword.toCharArray());
        }
        return (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());
    }
}