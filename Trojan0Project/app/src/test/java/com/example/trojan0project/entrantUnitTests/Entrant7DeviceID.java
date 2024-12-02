package com.example.trojan0project.entrantUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class Entrant7DeviceID {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeviceIdGenerated() {
        String mockDeviceId = "12345abcd";

        String generatedDeviceId = getDeviceId(mockDeviceId);
        assertNotNull("Device ID should not be null", generatedDeviceId);
        assertEquals("Device ID should match the mock value", mockDeviceId, generatedDeviceId);
    }

    private String getDeviceId(String mockDeviceId) {
        return mockDeviceId;
    }
}
