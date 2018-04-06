package main;

import amf.client.AMF;

import java.util.concurrent.ExecutionException;

public class Main {
    
    public static void main(String[] args) {
        try {
            AMF.init().get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    private final static String filePath = "file://src/main/resources/raml/api.raml";
}
