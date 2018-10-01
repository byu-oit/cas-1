package org.apereo.cas;

import org.apereo.cas.authentication.support.DefaultCasProtocolAttributeEncoder;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;

import lombok.val;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * This is test cases for {@link DefaultCasProtocolAttributeEncoder}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DefaultCasAttributeEncoderTests extends BaseCasCoreTests {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private Map<String, Object> attributes;

    @Autowired
    private ServicesManager servicesManager;

    private static Collection<String> newSingleAttribute(final String attr) {
        return Collections.singleton(attr);
    }

    @Before
    public void before() {
        this.attributes = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> this.attributes.put("attr" + i, newSingleAttribute("value" + i)));
        this.attributes.put(CasViewConstants.MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET, newSingleAttribute("PGT-1234567"));
        this.attributes.put(CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL, newSingleAttribute("PrincipalPassword"));
    }

    @Test
    public void checkNoPublicKeyDefined() {
        val service = RegisteredServiceTestUtils.getService("testDefault");
        val encoder = new DefaultCasProtocolAttributeEncoder(this.servicesManager, CipherExecutor.noOpOfStringToString());
        val encoded = encoder.encodeAttributes(this.attributes, this.servicesManager.findServiceBy(service));
        assertEquals(this.attributes.size() - 2, encoded.size());
    }

    @Test
    public void checkAttributesEncodedCorrectly() {
        val service = RegisteredServiceTestUtils.getService("testencryption");
        val encoder = new DefaultCasProtocolAttributeEncoder(this.servicesManager, CipherExecutor.noOpOfStringToString());
        val encoded = encoder.encodeAttributes(this.attributes, this.servicesManager.findServiceBy(service));
        assertEquals(encoded.size(), this.attributes.size());
        checkEncryptedValues(CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL, encoded);
        checkEncryptedValues(CasViewConstants.MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET, encoded);
    }

    private void checkEncryptedValues(final String name, final Map<String, Object> encoded) {
        val v1 = ((Collection<?>) this.attributes.get(
            name)).iterator().next().toString();
        val v2 = (String) encoded.get(name);
        assertNotEquals(v1, v2);
    }
}
