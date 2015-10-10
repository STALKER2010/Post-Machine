package io.github.stalker2010.post.vm;

public class StopOP extends OP
{
	public StopOP(final VM vm, final String op, final String[] args)
	{
		super(vm, op, args);
	}

	@Override
	public static boolean isApplicableOp(String op)
	{
		return op.equals("!");
	}

	@Override
	public boolean isCorrect()
	{
		return true;
	}

	@Override
	public void run()
	{
		stop();
	}
}
