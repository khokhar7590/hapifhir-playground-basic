import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SampleClientTest {

  private String testFileName = "./src/test/resources/LastNameTest.txt";

  @Test
  public void testReadFile(){
    List<String> expectedList = new ArrayList<>();
    expectedList.add("SMITH");
    expectedList.add("WARNER");
    List<String> actualList = SampleClient.readFile(testFileName);
    Assert.assertEquals(expectedList,actualList);
  }

  @Test
  public void testAverageTimeWithAndWithoutCaching(){
    List<String> listName = SampleClient.readFile(testFileName);
    IGenericClient client = FhirContext.forR4().newRestfulGenericClient("http://hapi.fhir.org/baseR4");
    Double firstAttemptAverageTime = SampleClient.getAverageResponseTime(listName,client,false).getAsDouble();
    Double secondAttemptWithCache = SampleClient.getAverageResponseTime(listName,client,false).getAsDouble();
    Double thirdAttemptWithoutCache = SampleClient.getAverageResponseTime(listName,client,true).getAsDouble();
    Assert.assertTrue(firstAttemptAverageTime>secondAttemptWithCache);
    Assert.assertTrue(secondAttemptWithCache<thirdAttemptWithoutCache);
  }
}
