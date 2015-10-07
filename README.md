# Guardian #
The Guardian is a Java Enterprise Security Enforcer.

Security is _enforced_ through Java invocation interception. Where the code and data under guard is _intercepted_ for authorization against an authenticated caller.

In many ways the Guardian is personified as a Servant Protector of your [Application's Services and Data Resources](https://github.com/3venthorizon/guardian/wiki/Resources). Meaning, your Application's Services and Data Resources are not subjects of the Guardian where they are constantly asking(code API invocation) for [Permission](https://github.com/3venthorizon/guardian/blob/master/guardian/core/src/main/java/co/dewald/guardian/realm/Permission.java). Rather, the Guardian protects([intercepts](https://github.com/3venthorizon/guardian/blob/master/guardian/gatekeeper/src/main/java/co/dewald/guardian/gate/Guard.java)) those Resources marked([annotated code](https://github.com/3venthorizon/guardian/blob/master/guardian/gatekeeper/src/main/java/co/dewald/guardian/gate/Grant.java)) and applies the security Permission rules & knowledge([Realm](https://github.com/3venthorizon/guardian/wiki/Resources)) contracted(setup) by you the custodian of the Application System.

# Entrust the Guardian
The Guardian can be entrusted to protect your **resources** in the following ways:
* Authentication - **Who** needs access?
* Authorization - **What** _resource_ is _guarded_?
* Auditing - _Who_ accessed _What_ **When**?
* **Filtering** - Sift and filter through the data passed to and from _resources_.
* Session Management - Track system's interactions between authentication and log-out.
* Administration - Tell the Guardian _Who_ may access _What_ and also _When_ they can do so.

# Guarding Resources
Guarding is an active process and appropriately described with the verb. The [@Guard](https://github.com/3venthorizon/guardian/blob/master/guardian/gatekeeper/src/main/java/co/dewald/guardian/gate/Guard.java) intercept annotation will indicate to the Guardian which Java class or method requires guarding. 

    import co.dewald.guardian.gate.Grant;
    import co.dewald.guardian.gate.Guard;

    @Guard
    @Grant(name = "machine") //rename the resource from OfficeMachine.class.getName() to machine
    public class OfficeMachine {
        //method names become the name of the action via reflection
        @Grant(name = "copy") //will rename the action and resolve possible overloading conflicts
        public void print(Document document) {
        //...
        }

        @Grant(check = false) //disable security checks for this action
        public Document scan() {
        //...
        }

        //filter the parameter number of the resource named phone# 
        @Grant(filter = true)
        public void fax(Document document, 
                        @Grant(name = "phone#", filter = true) String number) {
        //...
        }
    }

Conceptually a resource represents a security [Permission](https://github.com/3venthorizon/guardian/blob/master/guardian/core/src/main/java/co/dewald/guardian/realm/Permission.java) and is uniquely identifiable by a **resource - [action or value]** java.lang.String pair. [Resources](https://github.com/3venthorizon/guardian/wiki/Resources) are annotated on the Java [class, method, parameter] definition with the [@Grant](https://github.com/3venthorizon/guardian/blob/master/guardian/gatekeeper/src/main/java/co/dewald/guardian/gate/Grant.java) annotation. The actions that can be performed on a resource or class is defined by the class's methods. Method parameters and return values may also be viewed as respective Permissions. In which case the parameter or type class need to be annotated to indicate a resource while the java.lang.Object#toString() will define the value in the Permission java.lang.String pair.
