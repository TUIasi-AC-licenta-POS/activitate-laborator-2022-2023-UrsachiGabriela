package spotify.clients;

import com.spotify.idmclient.wsdl.*;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import spotify.utils.Urls;
import spotify.view.requests.LoginRequest;
import spotify.view.responses.LoginResponse;

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

    public LoginResponse login(LoginRequest loginRequest){
        Login login =new Login();
        login.setUname(OBJECT_FACTORY.createLoginUname(loginRequest.getName()));
        login.setUpass(OBJECT_FACTORY.createLoginUpass(loginRequest.getPassword()));

        JAXBElement<Login> request = OBJECT_FACTORY.createLogin(login);

        JAXBElement<com.spotify.idmclient.wsdl.LoginResponse> responseJAXBElement =
                (JAXBElement<com.spotify.idmclient.wsdl.LoginResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        com.spotify.idmclient.wsdl.LoginResponse response = responseJAXBElement.getValue();

        return new LoginResponse(response.getLoginResult().getValue());
    }

    public boolean logout(LoginResponse logoutRequest){
        Logout logout = new Logout();
        logout.setAccessToken(OBJECT_FACTORY.createLogoutAccessToken(logoutRequest.getJwsToken()));

        JAXBElement<Logout> request = OBJECT_FACTORY.createLogout(logout);

        JAXBElement<LogoutResponse> responseJAXBElement = (JAXBElement<LogoutResponse>) getWebServiceTemplate().marshalSendAndReceive(Urls.IDM_REQUEST_URL,request);

        LogoutResponse response = responseJAXBElement.getValue();

        return response.getLogoutResult().getValue();
    }
}
