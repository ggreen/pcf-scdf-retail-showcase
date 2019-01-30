package io.pivotal.gemfire.domain;

public class ProductAssociate
{
	public ProductAssociate()
	{}

	
	public ProductAssociate( String pre,String[] post)
	{
		super();
		this.post = post;
		this.pre = pre;
	}


	/**
	 * @return the post
	 */
	public String[] getPost()
	{
		return post;
	}
	/**
	 * @return the pre
	 */
	public String getPre()
	{
		return pre;
	}
	/**
	 * @param post the post to set
	 */
	public void setPost(String[] post)
	{
		this.post = post;
	}
	/**
	 * @param pre the pre to set
	 */
	public void setPre(String pre)
	{
		this.pre = pre;
	}
	private String[] post;
	private String pre;
}
