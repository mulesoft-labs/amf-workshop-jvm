package main;

import amf.ProfileNames;
import amf.client.AMF;
import amf.client.model.document.BaseUnit;
import amf.client.model.document.Document;
import amf.client.model.domain.DomainElement;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;
import amf.client.model.domain.WebApi;
import amf.client.parse.RamlParser;
import amf.client.validate.ValidationReport;
import amf.client.validate.ValidationResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    
    public static void main(String[] args) {
        try {
            AMF.init().get();

            final RamlParser ramlParser = AMF.ramlParser();

            final BaseUnit baseUnit = ramlParser.parseFileAsync(filePath).get();

            final Document document = (Document) baseUnit;

            WebApi webApi = (WebApi)document.encodes();
            final ValidationReport validationReport = AMF.validate(baseUnit, ProfileNames.RAML(), ProfileNames.RAML()).get();
            System.out.println(validationReport.conforms());
            final List<ValidationResult> results = validationReport.results();
            for (ValidationResult result : results) {
                System.out.println(result);
            }

            final ScalarShape schema = (ScalarShape)webApi.endPoints().get(0).operations().get(0).request().payloads().get(0).schema();
            schema.withMaxLength(-1);

            final ValidationReport validationReport2 = AMF.validate(baseUnit, ProfileNames.RAML(), ProfileNames.RAML()).get();
            System.out.println(validationReport2.conforms());
            final List<ValidationResult> results2 = validationReport.results();
            for (ValidationResult result : results2) {
                System.out.println(result);
            }

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
