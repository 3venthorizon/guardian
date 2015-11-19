package co.dewald.guardian.gwt.rpc;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import co.dewald.guardian.gate.Administration;


/**
 * 
 * @author Dewald Pretorius
 */
@RemoteServiceRelativePath("guardian/bureaucrat")
public interface AdminRPC extends Administration, RemoteService {

}
