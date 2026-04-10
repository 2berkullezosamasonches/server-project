package com.warehouse.warehouse_manager.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Objects;

@Slf4j
@Component
public class KeyProvider {

    private final ResourceLoader resourceLoader;

    @Value("${signature.keystore.path}")
    private String keystorePath;

    @Value("${signature.keystore.password}")
    private String keystorePassword;

    @Value("${signature.keystore.type:PKCS12}")
    private String keystoreType;

    @Value("${signature.key.alias}")
    private String keyAlias;

    @Value("${signature.key.password:}")
    private String keyPassword;

    private volatile PrivateKey privateKey;
    private volatile X509Certificate certificate;
    private volatile PublicKey publicKey;

    public KeyProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        loadKeys();
    }

    public PrivateKey getPrivateKey() {
        ensureLoaded();
        return privateKey;
    }

    public PublicKey getPublicKey() {
        ensureLoaded();
        return publicKey;
    }

    public X509Certificate getCertificate() {
        ensureLoaded();
        return certificate;
    }

    private void ensureLoaded() {
        if (privateKey == null || certificate == null || publicKey == null) {
            synchronized (this) {
                if (privateKey == null || certificate == null || publicKey == null) {
                    loadKeys();
                }
            }
        }
    }

    private void loadKeys() {
        try {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);

            try (InputStream inputStream = openKeystoreStream()) {
                keyStore.load(inputStream, keystorePassword.toCharArray());
            }

            String effectiveKeyPassword = (keyPassword == null || keyPassword.isBlank())
                    ? keystorePassword
                    : keyPassword;

            Key key = keyStore.getKey(keyAlias, effectiveKeyPassword.toCharArray());
            if (!(key instanceof PrivateKey pk)) {
                throw new IllegalStateException("Ключ с alias '" + keyAlias + "' не является приватным ключом");
            }

            Certificate cert = keyStore.getCertificate(keyAlias);
            if (cert == null) {
                throw new IllegalStateException("Сертификат с alias '" + keyAlias + "' не найден в keystore");
            }
            if (!(cert instanceof X509Certificate x509Certificate)) {
                throw new IllegalStateException("Сертификат с alias '" + keyAlias + "' не является X509Certificate");
            }

            this.privateKey = pk;
            this.certificate = x509Certificate;
            this.publicKey = x509Certificate.getPublicKey();

            log.info("Keystore loaded successfully. alias={}, type={}, subject={}",
                    keyAlias, keystoreType, x509Certificate.getSubjectX500Principal().getName());

        } catch (Exception e) {
            throw new IllegalStateException("Не удалось загрузить keystore/ключи для ЭЦП: " + e.getMessage(), e);
        }
    }

    private InputStream openKeystoreStream() throws Exception {
        Resource resource = resolveResource(keystorePath);
        if (!resource.exists()) {
            throw new IllegalStateException("Keystore не найден по пути: " + keystorePath);
        }
        return resource.getInputStream();
    }

    private Resource resolveResource(String path) {
        Objects.requireNonNull(path, "Путь к keystore не должен быть null");

        if (path.startsWith("classpath:") || path.startsWith("file:")) {
            return resourceLoader.getResource(path);
        }

        Resource classpathResource = resourceLoader.getResource("classpath:" + path);
        if (classpathResource.exists()) {
            return classpathResource;
        }

        return resourceLoader.getResource("file:" + path);
    }
}