package parsec;

public class Nothing<T> extends Maybe<T> {
	@Override
	public Boolean isJust()
	{
		return false;
	}
}
