package org.javalobby.validation;

/**
 * This interface provides a way for our validators to trigger any dialog specific 
 * events you might want performed when the form is in an invalid state--for example, 
 * disabling Ok and/or Apply buttons. It also provides a way to trigger any dialog 
 * specific events you want performed when the form returns to a valid state, 
 * such as re-enabling those buttons.<br>
 * Implementing this interface is optional, but if you don't do it, your dialog will 
 * not receive any notification of current validation status. Note that it is not strictly 
 * necessary to disable Ok and Apply buttons, since InputVerifier won't let them actually 
 * do anything if there is invalid data. But I think disabling them is a nice esthetic touch. 
 * With the interface out of the way, lets move on to the abstract class. 
 * 
 * @author Michael Urban
 *
 */
public interface WantsValidationStatus 
{
  void validateFailed();  // Called when a component has failed validation.
  void validatePassed();  // Called when a component has passed validation.
}

