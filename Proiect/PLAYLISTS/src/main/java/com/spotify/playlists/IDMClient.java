package com.spotify.playlists;

import com.spotify.idmclient.wsdl.GetUserInfo;
import com.spotify.idmclient.wsdl.GetUserInfoResponse;
import com.spotify.idmclient.wsdl.ObjectFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

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
}
