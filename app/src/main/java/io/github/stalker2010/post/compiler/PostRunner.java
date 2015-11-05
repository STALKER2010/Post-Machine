package io.github.stalker2010.post.compiler;

public class PostRunner
{
	public String code;
	public Class<? extends PostClass> cl;
	public void run() {
		try
		{
			PostClass c = cl.newInstance();
			c.run();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
