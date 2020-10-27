package com.example.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class ZipDownload implements RequestStreamHandler {

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	/**
	 * Downloading Zipped archive in response stream
	 */
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        
        LambdaLogger logger = context.getLogger();
        logger.log("in lambda function \n");
        MimeTypes allMimeTypes = MimeTypes.getDefaultMimeTypes();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
        OutputStream encodedStream = Base64.getEncoder().wrap(outputStream);
        
        try {        	
        	HashMap event = gson.fromJson(bufferedReader, HashMap.class);
        	logger.log(String.format("params: %s", event.get("files").toString()));
        	List<Map> filesList = (List<Map>)event.get("files");        	
        	
        	ZipOutputStream zipOutputStream = new ZipOutputStream(encodedStream);
        			        	
        	for (Map<String, String> file : filesList) {
        		URL fileURLObj = new URL(file.get("url"));
        		String fileName = FilenameUtils.getBaseName(fileURLObj.getPath());
        		fileName = fileName + allMimeTypes.forName(file.get("contentType")).getExtension();
        		
        		InputStream fileInputStream = fileURLObj.openStream();        		
        		logger.log("working on file: " + fileName);
        		ZipEntry zipEntry = new ZipEntry(fileName);
        		zipOutputStream.putNextEntry(zipEntry);
        		ByteStreams.copy(fileInputStream, zipOutputStream);
        		zipOutputStream.closeEntry();      
        		logger.log("completed processing for " + fileName);
        	}        	
        	
        	zipOutputStream.finish();
        	zipOutputStream.close();        	
        	
        } catch (IllegalStateException | JsonSyntaxException | MimeTypeException exception) {
        	logger.log(exception.toString());
        } finally {
        	bufferedReader.close();
        	encodedStream.close();
        }
    }

}
