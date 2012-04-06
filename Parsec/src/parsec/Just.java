package parsec;

public class Just<T> extends Maybe<T> {
	public T result;
	public Just(T p)
	{
		this.result = p;
	}
	@Override
	public Boolean isJust() 
	{
		return true;
	}
}
