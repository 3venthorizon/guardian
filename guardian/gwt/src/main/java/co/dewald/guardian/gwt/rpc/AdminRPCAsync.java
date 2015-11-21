package co.dewald.guardian.gwt.rpc;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface AdminRPCAsync {

    void activatePermission(String resource, String action, boolean active, AsyncCallback<Void> callback);

    void mapRolePermissions(String role, String resource, String action, AsyncCallback<Void> callback);

    void mapSubjectRoles(String username, Set<String> roles, AsyncCallback<Void> callback);

    void registerPermission(String resource, String action, boolean active, AsyncCallback<Void> callback);

    void registerRole(String group, Integer calendarField, Integer start, Integer end, AsyncCallback<Void> callback);

    void registerSubject(String username, String password, AsyncCallback<Void> callback);

    void removePermission(String resource, String action, AsyncCallback<Void> callback);

    void removeRole(String role, AsyncCallback<Void> callback);

    void removeRolePermission(String role, String resource, String action, AsyncCallback<Void> callback);

    void removeSubject(String username, AsyncCallback<Void> callback);

    void removeSubjectRole(String username, String role, AsyncCallback<Void> callback);

    void updateRole(String exsisting, String role, Integer calendarField, Integer start, Integer end,
                    AsyncCallback<Void> callback);

    void updateSubject(String existing, String username, String password, AsyncCallback<Void> callback);

    void viewPermissionRoles(String resource, String action, AsyncCallback<List<String>> callback);

    void viewPermissions(AsyncCallback<Map<String, Set<String>>> callback);

    void viewRolePermissions(String role, AsyncCallback<Map<String, Set<String>>> callback);

    void viewRoleSubjects(String role, AsyncCallback<List<String>> callback);

    void viewRoles(AsyncCallback<List<String>> callback);

    void viewSubjectRoles(String username, AsyncCallback<List<String>> callback);

    void viewSubjects(AsyncCallback<List<String>> callback);

}
