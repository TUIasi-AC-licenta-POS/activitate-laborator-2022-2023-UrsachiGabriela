package spotify.services.authorization;

import com.spotify.idmclient.wsdl.*;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import spotify.utils.Urls;

import javax.xml.bind.JAXBElement;

public class IDMClient  extends WebServiceGatewaySupport {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public AuthorizeResp authorizeUser(String jwsToken){
        Authorize authorize = new Authorize();
        authorize.setAccessToken(OBJECT_FACTORY.createAuthorizeAccessToken(jwsToken));

        JAXBElement<Authorize> request = OBJECT_FACTORY.createAuthorize(authorize);
        System.out.println(request);

        getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        JAXBElement<AuthorizeResponse> responseJAXBElement = (JAXBElement<AuthorizeResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);
        AuthorizeResponse response = responseJAXBElement.getValue();

        return response.getAuthorizeResult().getValue();
    }
}
