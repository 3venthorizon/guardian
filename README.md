# Guardian #
*Transparent* Enterprise Security **Activation**.

At last! A security framework without the laborious and repetitive integration of API invocations messing up your perfect code base.

That's right. **Activate, *don't Integrate.* **

[TOC]

## Conceptual Difference: Role vs Resource Oriented Context ##
### Typical Security Frameworks ###
Typical security frameworks requires the developer to* be aware of the Users and Roles prior to or during development* of the business solution. This *Role Oriented Context* predefine the security tiers and dilutes the business logic with hard wired security logic. Hence, integration is required which effectively makes the business logic security aware.

This raises one question:

* How do we create business logic that is agnostic of security? 

### Resource Oriented Context ###
The Guardian conceptually differs in this regard that the the security tiers can be defined long after the fact and can be changed during run-time without rewriting or restarting due to configuration changes. This is possible due to the fact that **business resources are one to one mappings to security permissions**. One can thus dynamically group Permissions into Roles and associate roles to the User.

Now the security is transparent to the business logic. And the business logic is now longer aware of the security. The way it should be: **Activated**

$ git clone https://Jediknight@bitbucket.org/Jediknight/guardian.git/wiki

# Overview #
Imagine this typical integration scenario: You have an application which restricts a user from performing an **action** on a **resource**. Example: Printing(action) a document on the networked Office Copier/Scanner/Fax machine(Resource). 

Business Logic: You will probably have a class representing the Multi-function Office machine with methods to perform the respective functions of printing, copying, scanning and faxing.


```
#!java

public class OfficeMachine {
    public void print(Document document) {
    ...
    }

    public Document scan() {
    ...
    }

    public void fax(Document document, String number) {
    ...
    }
}
```


## Integration Legacy ##
Restricting the functions each user have access to, you may have to invoke some security API that returns a result from within each defined method. Based on the API result you will have some standard code logic that wraps the business logic and determines if it will be executed or not. In other words, lots and lots of boilerplate code that varies ever so slightly.

```
#!java

if (SecurityAPI.authorised(username, "OfficeMachine", "print")) {
   officeMachine.print(document);
} else {
    throw new SecurityException("Unauthorised Access to print documents for user: " + username.toString());
}
```

So what do you do when you have not just 1 resource with a couple of actions, but a vast many resources and data upon which many more actions can be performed? You can enforce meticulous bureaucracy, but me; I will just go crazy.

## Complex Security Requirements ##
The previous example is quite simple. Imagine what the code would look like when you have to place restrictions based on time of day or filter/strip parameter data and result lists prior and post business logic execution. 

* How does your current security framework handle time constrained authorisation?
* How about filtering or shaping parameter data before an action is executed?
* Does it filter return results after an action has been performed?
  
# Interceptor Activation #
The Java EE provides CDI Interceptors and Decorators which enables the Guardian to Activate security. With only a couple of annotations you mark or declare classes, methods and parameters as guarded. The code sample below illustrates this concept of **activating** security without *integration* logic:

```
#!java

@Guard
@Grant(name = "machine") //rename the resource from OfficeMachine.class.getName() to machine
public class OfficeMachine {
    //method names become the name of the action via reflection
    @Grant(name = "copy") //will rename the action and resolve possible overloading conflicts
    public void print(Document document) {
    ...
    }

    @Grant(check = false) //disable security checks for this action
    public Document scan() {
    ...
    }

    //filter the parameter number of the resource named phone# 
    @Grant(filter = true)
    public void fax(Document document, @Grant(name = "phone#", filter = true) String number) {
    ...
    }
}
```

# Components #
The core security components that make up the Guardian are listed below:

## [Logic](https://bitbucket.org/Jediknight/guardian/wiki/Logic)
## [Realm](https://bitbucket.org/Jediknight/guardian/wiki/Realm)
## Interceptor
## Session
## [Auditor](https://bitbucket.org/Jediknight/guardian/wiki/Auditor)
## Policy
## Administration