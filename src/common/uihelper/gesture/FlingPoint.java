package common.uihelper.gesture;

public class FlingPoint
{
	float x;
	float y;

	public FlingPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString()
	{
		return "FlingPoint [x=" + x + ", y=" + y + "]";
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}
}
