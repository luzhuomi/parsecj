package parsec;

// function composition f0 . f1 = f0(f1(x))

public class FunctionComposition<A,B,C> implements Function<A, C> {
	public Function<A,B> f0;
	public Function<B,C> f1;
	public FunctionComposition(Function<A,B> f0, Function<B,C> f1 )
	{
		this.f0 = f0;
		this.f1 = f1;
	}
	public A apply(C c)
	{
		B b = f1.apply(c);
		A a = f0.apply(b);
		return a;
	}
}
