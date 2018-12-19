package org.apereo.cas.trusted.authentication.storage;

import org.apereo.cas.category.FileSystemCategory;
import org.apereo.cas.trusted.AbstractMultifactorAuthenticationTrustStorageTests;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;


/**
 * This is {@link JsonMultifactorAuthenticationTrustStorageTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = "cas.authn.mfa.trusted.json.location=file:/tmp/trusted-device.json")
@Category(FileSystemCategory.class)
@Getter
public class JsonMultifactorAuthenticationTrustStorageTests extends AbstractMultifactorAuthenticationTrustStorageTests {

    @Autowired
    @Qualifier("mfaTrustEngine")
    protected MultifactorAuthenticationTrustStorage mfaTrustEngine;

    @BeforeClass
    @SneakyThrows
    public static void beforeClass() {
        deleteJsonFile();
    }

    @AfterClass
    @SneakyThrows
    public static void afterClass() {
        deleteJsonFile();
    }

    private static void deleteJsonFile() throws IOException {
        val file = new File("/tmp/trusted-device.json");
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }
}
