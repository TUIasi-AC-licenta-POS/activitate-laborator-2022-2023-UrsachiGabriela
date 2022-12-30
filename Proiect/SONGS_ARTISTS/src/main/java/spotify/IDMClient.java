package spotify;

import com.spotify.idmclient.wsdl.*;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import spotify.utils.Urls;

import javax.xml.bind.JAXBElement;

public class IDMClient  extends WebServiceGatewaySupport {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public String getUserInfoResponse(String name){
        GetUserInfo getUserInfo = new GetUserInfo();
        getUserInfo.setUname(OBJECT_FACTORY.createGetUserInfoUname(name));

        JAXBElement<GetUserInfo> request = OBJECT_FACTORY.createGetUserInfo(getUserInfo);
        System.out.println(request);

        getWebServiceTemplate().marshalSendAndReceive("http://127.0.0.1:8000", request);
        JAXBElement<GetUserInfoResponse> response = (JAXBElement<GetUserInfoResponse>) getWebServiceTemplate().marshalSendAndReceive("http://127.0.0.1:8000", request);
        GetUserInfoResponse response1 = response.getValue();
        String naaamee = response1.getGetUserInfoResult().getValue().getUname().getValue();

        System.out.println(response);
        //
        // return response.getGetUserInfoResult().getValue().getUname().getValue();
        return naaamee;
    }

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
