package main;

import amf.ProfileNames;
import amf.client.AMF;
import amf.client.model.document.BaseUnit;
import amf.client.model.document.Document;
import amf.client.model.domain.*;
import amf.client.parse.Oas20Parser;
import amf.client.parse.RamlParser;
import amf.client.render.Oas20Renderer;
import amf.client.validate.ValidationReport;
import amf.core.vocabulary.Namespace;
import amf.core.vocabulary.ValueType;
import amf.plugins.document.webapi.parser.spec.oas.OasParameter;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    
    public static void main(String[] args) {
        try {
            AMF.init().get();
            final RamlParser ramlParser = AMF.ramlParser();
            final BaseUnit ramlUnit = ramlParser.parseFileAsync(ramlFile).get();
            final Document ramlDocument = (Document)ramlUnit;
            
            System.out.println("BaseUnit location: "+ ramlUnit.location());
            final WebApi ramlApi = (WebApi) ramlDocument.encodes();
            final AnyShape anyShape = (AnyShape) ramlDocument.declares().get(0);
            
            System.out.println("Raml document location: "+ ramlApi.name());
            System.out.println("Raml api first endpoint: "+ ramlApi.endPoints().get(0).name());
            System.out.println("Raml document first declared type name: "+ anyShape.name());
    
            final Oas20Renderer oas20Renderer = AMF.oas20Generator();
            final String rendererOas = oas20Renderer.generateString(ramlDocument).get();
            System.out.println("Raml document renderer to Oas api: "+ rendererOas);
    
            //parse again raml or oas
            final ValidationReport failingReport = AMF.validate(ramlUnit, ProfileNames.RAML(), ProfileNames.RAML()).get();
            System.out.println("Validation report conforms: "+ failingReport.conforms());
    
            
            final ScalarShape schema = (ScalarShape)ramlApi.endPoints().get(0).operations().get(0).responses().get(0).payloads().get(0).schema();
            schema.withMaxLength(5);
    
            final ValidationReport fixedReport = AMF.validate(ramlUnit, ProfileNames.RAML(), ProfileNames.RAML()).get();
            System.out.println("Validation report after fix conforms: "+ fixedReport.conforms());
    
            // parse again
            final BaseUnit resolved = AMF.resolveRaml10(ramlDocument);
            final Document resolvedDoc = (Document)resolved;
            System.out.println("Validation resolved declares: "+ resolvedDoc.declares().size());
            final WebApi resolvedApi = (WebApi) resolvedDoc.encodes();
            
            System.out.println("Validation resolved endpoint: "+
                    resolvedApi.endPoints().get(0).operations().size());
    
            final String renderedGraph = AMF.amfGraphGenerator().generateString(resolved).get();
            

//            final Oas20Parser oasParser = AMF.oas20Parser();
//            final BaseUnit oasUnit = oasParser.parseFileAsync(ramlFile).get();
//
//            System.out.println("BaseUnit location: "+ oasUnit.location());
    
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    final static String ramlFile = "file://src/main/resources/raml/api.raml";
    final static String oasFile = "file://src/main/resources/oas/api.json";
}
