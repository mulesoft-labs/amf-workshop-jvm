package main;

import amf.client.AMF;
import amf.client.model.document.BaseUnit;
import amf.client.model.document.Document;
import amf.client.model.domain.DomainElement;
import amf.client.parse.RamlParser;

import java.util.concurrent.ExecutionException;

public class Main {
    
    public static void main(String[] args) {
        try {
            AMF.init().get();

            final RamlParser ramlParser = AMF.ramlParser();

            final BaseUnit baseUnit = ramlParser.parseFileAsync(filePath).get();

            final Document document = (Document) baseUnit;

            final DomainElement encodes = document.encodes();

            System.out.println(document.platform());

            final String s = AMF.oas20Generator().generateString(baseUnit).get();
            s.length();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    private final static String filePath = "file://src/main/resources/raml/api.raml";
}
