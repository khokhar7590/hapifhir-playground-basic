import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

public class SampleClient {

    private final static Logger log = LoggerFactory.getLogger(SampleClient.class.getName());

    public static void main(String[] theArgs) {

        // Create a FHIR client
        IGenericClient client = FhirContext.forR4().newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        String fileName = "./src/main/resources/LastNames.txt";
        List<String> lastNames = readFile(fileName);
        boolean disableCache = false;

        for (int i=1; i<=3;i++){
            if (i == 3) disableCache = true;
            OptionalDouble averageResponseTime = getAverageResponseTime(lastNames,client,disableCache);
            log.info("Average Response Time for attempt {} is {} ms",i,averageResponseTime.orElse(0));
        }
    }

    public static List<String> readFile(String fileName){
        List<String> lastNames = Collections.emptyList();
        try {
            lastNames = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return  lastNames;
    }

    public static OptionalDouble getAverageResponseTime(List<String> lastNames, IGenericClient client,boolean disableCache){

        ClientInterceptor interceptor = new ClientInterceptor();
        client.registerInterceptor(interceptor);
        CacheControlDirective controlDirective = new CacheControlDirective();
        if (disableCache) controlDirective.setNoCache(true);

        return  lastNames.stream().mapToInt(s -> {
            client.search()
                    .forResource("Patient")
                    .where(Patient.FAMILY.matches().value(s))
                    .cacheControl(controlDirective)
                    .returnBundle(Bundle.class)
                    .execute();
            return interceptor.requestStopWatch().intValue();
        }).average();
    }

}
