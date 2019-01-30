package io.pivotal.gemfire.domain;

public class CustomerIdentifier
{
	private String key;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	

	public CustomerIdentifier()
	{}
	
	
	public CustomerIdentifier(String key, String firstName, String lastName, String mobileNumber)
	{
		super();
		this.key = key;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobileNumber = mobileNumber;
	}


	/**
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key)
	{
		this.key = key;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}
	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber()
	{
		return mobileNumber;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber)
	{
		this.mobileNumber = mobileNumber;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("CustomerIdentifier [firstName=").append(firstName).append(", lastName=").append(lastName)
		.append(", mobileNumber=").append(mobileNumber).append("]");
		return builder.toString();
	}
	
	
	
}
