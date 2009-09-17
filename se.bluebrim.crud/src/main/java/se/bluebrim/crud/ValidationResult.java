package se.bluebrim.crud;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/** 
 * Class that contains the result of an validation, 
 * that is the unlocalized messages (as keys) and the resource bundle to use to localize them
 * @author OPalsson
 *
 */
public class ValidationResult
{
	private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
	private ResourceBundle bundle;
	
	public ValidationResult(ResourceBundle bundle)
	{
		this.bundle = bundle;
	}

	public ValidationResult(ResourceBundle bundle, List<ValidationMessage> messages)
	{
		this.bundle = bundle;
		this.messages = messages;
	}

	public void add(ValidationMessage message)
	{
		messages.add(message);
	}

	public void add(String key, Object... parameters)
	{
		messages.add(new ValidationMessage(key, parameters));
	}

	
	public void add(List<ValidationMessage> messages)
	{
		this.messages.addAll(messages);
	}

	public void addErrorKeys(List<String> errorKeys)
	{
		for (String errorKey : errorKeys)
			this.messages.add(new ValidationMessage(errorKey));
	}

	public List<String> getLocalizedMessagesOfType(ValidationMessage.Type type)
	{
		List<String> localized = new ArrayList<String>();
		for (ValidationMessage message : messages)
		{
			if (message.getType() == type)
				localized.add(MessageFormat.format(bundle.getString(message.getKey()), message.getParameters()));
		}
		return localized;
	}
	
	public boolean hasMessagesOfType(ValidationMessage.Type type)
	{
		for (ValidationMessage message : messages)
		{
			if (message.getType() == type)
				return true;
		}
		return false;
	}

}
