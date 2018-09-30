package com.example.fernando.SimpleAPP;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreatedTimeHelperTest {

    private Context instrumentationCtx;

    @Before
    public void setUp() {
        instrumentationCtx = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void getDate_Test() {
        CreatedTimeHelper helper = new CreatedTimeHelper(instrumentationCtx);

        String result = helper.getDate(1);
        assertEquals("48 years ago", result);
    }
}