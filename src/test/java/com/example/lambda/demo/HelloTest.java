package com.example.lambda.demo;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class HelloTest {

    private static Object input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    @Ignore
    public void testHello() {
        ZipDownload handler = new ZipDownload();
        Context ctx = createContext();

        //String output = handler.handleRequest(inputStream, outputStream, context);(input, ctx);

        // TODO: validate output here if needed.
        //Assert.assertEquals("Hello from Lambda!", output);
    }
}
