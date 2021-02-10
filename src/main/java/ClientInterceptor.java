import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

import java.io.IOException;

public class ClientInterceptor implements IClientInterceptor {

  Long startTime;
  Long endTime;

  @Override
  public void interceptRequest(IHttpRequest iHttpRequest) {
    startTime = System.currentTimeMillis();
  }

  @Override
  public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {
    endTime = System.currentTimeMillis();
  }

  public Long requestStopWatch()  {
    return endTime - startTime;
  }
}
