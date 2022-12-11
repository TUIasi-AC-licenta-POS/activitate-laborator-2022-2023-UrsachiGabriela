package spotify.services;


import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import soap.idm.services.GetUserInfo;
import soap.idm.services.GetUserInfoResponse;
import soap.idm.services.ObjectFactory;

import javax.xml.bind.JAXBElement;


public class IDMClient extends WebServiceGatewaySupport {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private WebClient webClient;


    public GetUserInfoResponse getUserInfo(String username) {
        GetUserInfo request = new GetUserInfo();
        request.setUname(new ObjectFactory().createGetUserInfoUname(username));

        //Object ob = getWebServiceTemplate().marshalSendAndReceive(new JAXBElement<>(new QName("services.IDM.soap", "uname"),String.class,username));
        GetUserInfoResponse response = (GetUserInfoResponse) getWebServiceTemplate().marshalSendAndReceive(request);

        return response;
    }

    public String getUserIdByName(String username) {
        GetUserInfoResponse getUserInfoResponse;
        ObjectFactory objectFactory = new ObjectFactory();
        GetUserInfo getUserInfoRequest = objectFactory.createGetUserInfo();

        // https://stackoverflow.com/questions/19548374/set-a-value-for-jaxbelementstring
        JAXBElement<String> jaxbElement = objectFactory.createGetUserInfoUname(username);
        getUserInfoRequest.setUname(jaxbElement);

        getUserInfoResponse = (GetUserInfoResponse) getWebServiceTemplate().marshalSendAndReceive(getUserInfoRequest);


        return getUserInfoResponse
                .getGetUserInfoResult()
                .getValue()
                .getUid()
                .getValue()
                .toString();
    }
}
