package co.dewald.guardian.ws;


import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import co.dewald.guardian.gate.Administration;
import co.dewald.guardian.gate.Guardian;


@WebService(serviceName = "AdminService",
            targetNamespace = "http://ws.guardian.admin.dewald.co",
            portName = "GuardianAdmin")
public class AdminWS {
    
    @EJB Administration admin;
    @EJB Guardian guardian;
    
    @WebMethod
    @WebResult(name = "success")
    public boolean authenticate(@WebParam(name = "username") String username, 
                                @WebParam(name = "password") String password) {
        Boolean authenticated = guardian.authenticate(username, password);
        return Boolean.TRUE.equals(authenticated);
    }
    
    @WebMethod
    @WebResult(name = "token")
    public String login(@WebParam(name = "username") String username, 
                        @WebParam(name = "password") String password) {
        Boolean authenticated = guardian.authenticate(username, password);
        
        if (Boolean.TRUE.equals(authenticated)) return guardian.getSessionToken(username);
        return "Authentication Failed";
    }

    @WebMethod
    public void registerSubject(@WebParam(name = "username") String username, 
                                @WebParam(name = "password") String password) {
        admin.registerSubject(username, password);
    }

    @WebMethod
    @WebResult(name = "subjects")
    public List<String> viewSubjects() {
        return admin.viewSubjects();
    }
}
