package spotify.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import soap.idm.services.GetUserInfo;
import soap.idm.services.GetUserInfoResponse;
import spotify.services.IDMClient;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SoapClientConfig {

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return messageFactory;
    }

    @Bean
    public Jaxb2Marshaller marshaller()  {

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(GetUserInfo.class, GetUserInfoResponse.class);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jaxb.formatted.output", true);
        jaxb2Marshaller.setMarshallerProperties(map);

        //jaxb2Marshaller.setCheckForXmlRootElement(false);

        return jaxb2Marshaller;
    }

    @Bean
    public IDMClient idmClientService(Jaxb2Marshaller jaxb2Marshaller,SaajSoapMessageFactory messageFactory) {

        IDMClient idmClient = new IDMClient();
        idmClient.setDefaultUri("http://127.0.0.1:8000");
        idmClient.setMarshaller(jaxb2Marshaller);
        idmClient.setUnmarshaller(jaxb2Marshaller);
        idmClient.setMessageFactory(messageFactory);
        return idmClient;
    }
}
