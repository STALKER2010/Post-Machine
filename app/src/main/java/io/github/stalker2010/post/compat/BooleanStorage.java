package io.github.stalker2010.post.compat;
import java.util.*;

public class BooleanStorage
{
    private int[] mKeys;
    public BooleanStorage()
	{
        mKeys = new int[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public BooleanStorage clone()
	{
        BooleanStorage clone = null;
        try
		{
            clone = (BooleanStorage) super.clone();
            clone.mKeys = mKeys.clone();
        }
		catch (CloneNotSupportedException cnse)
		{
            /* ignore */
        }
        return clone;
    }

    public boolean get(int key)
	{
        int i = ContainerHelpers.binarySearch(mKeys, mKeys.length, key);

		return i >= 0;
    }

    public void put(int key, boolean value)
	{
        int i = ContainerHelpers.binarySearch(mKeys, mKeys.length, key);

        if (i >= 0)
		{
			if (!value)
			{
				removeAt(i);
			}
        }
		else
		{
			if (value)
			{
				i = ~i;
				
				int[] newArray = ArrayUtils.newUnpaddedIntArray(mKeys.length + 1);
				System.arraycopy(mKeys, 0, newArray, 0, i);
				newArray[i] = key;
				System.arraycopy(mKeys, i, newArray, i + 1, mKeys.length - i);
				mKeys = newArray;
//				mKeys = GrowingArrayUtils.insert(mKeys, mKeys.length, i, key);
			}
        }
    }
	public void delete(int key)
	{
		mKeys = ArrayUtils.removeInt(mKeys, key);
    }
	public void removeAt(int i)
	{
		final int N = mKeys.length;
		int[] ret = new int[N - 1];
		if (i > 0)
		{
			System.arraycopy(mKeys, 0, ret, 0, i);
		}
		if (i < (N - 1))
		{
			System.arraycopy(mKeys, i + 1, ret, i, N - i - 1);
		}
		mKeys = ret;
	}

    public void remove(int key)
	{
        delete(key);
    }

	public int size()
	{
        return mKeys.length;
    }

    public void clear()
	{
        mKeys = new int[0];
    }

}
