package main;

import amf.ProfileNames;
import amf.client.AMF;
import amf.client.handler.Handler;
import amf.client.model.document.BaseUnit;
import amf.client.model.document.Document;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;
import amf.client.model.domain.WebApi;
import amf.client.parse.Oas20Parser;
import amf.client.parse.RamlParser;
import amf.client.validate.ValidationReport;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.apache.jena.base.Sys;

import java.util.concurrent.ExecutionException;

public class Main {
    
    public static void main(String[] args) {
        try {
            AMF.init().get();
    
            final RamlParser ramlParser = AMF.ramlParser();
    
            final BaseUnit baseUnit = ramlParser.parseFileAsync(filePath).get();
            final Document document = (Document)baseUnit;
            final WebApi webApi = (WebApi) document.encodes();
    
            final ValidationReport validationReport = AMF.validate(baseUnit, ProfileNames.RAML(), ProfileNames.RAML()).get();
    
            final ScalarShape schema = (ScalarShape)webApi.endPoints().get(0).operations().get(0).request().payloads().get(0).schema();
            schema.withMaxLength(5);
            
            
            final ValidationReport validationReportAfterFix = AMF.validate(baseUnit, ProfileNames.RAML(), ProfileNames.RAML()).get();
    
            System.out.println("Report conforms: " + validationReportAfterFix.conforms());
            System.out.print("document loc:" + document.location());
            System.out.print("web api title: "+ webApi.name());
    
            final String oasApi = AMF.oas20Generator().generateString(baseUnit).get();
            System.out.println("Oas rendered: "+ oasApi);
            
            
    
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    private final static String filePath = "file://src/main/resources/raml/api.raml";
    private final static String oasPath = "file://src/main/resources/oas/api.json";
}
