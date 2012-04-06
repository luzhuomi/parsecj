package parsec;

import java.util.ArrayList;
import java.util.List;

public class Parsec<T> extends Monad {
	public Function<Maybe<Pair<T,String>>,String> run;
	public Parsec(Function<Maybe<Pair<T,String>>,String> r)
	{
		this.run = r;
	}
	public Parsec<T> bind(final Function<Parsec<T>,T> bindee)
	{ // bind function for monad
		Function<Maybe<Pair<T,String>>,String> run0 = this.run;
		Function<Maybe<Pair<T,String>>,String> run1 = new FunctionComposition<Maybe<Pair<T,String>>, Maybe<Pair<T,String>>, String>(
				new Function<Maybe<Pair<T,String>>,Maybe<Pair<T,String>>> ()
				{ 
					public Maybe<Pair<T,String>> apply(Maybe<Pair<T,String>> mb)
					{
						if (mb.isJust())
						{
							Just<Pair<T,String>> j = (Just<Pair<T,String>>)mb;
							T f = j.result.fst;
							String s = j.result.snd;
							Parsec<T> p = bindee.apply(f);
							return p.run.apply(s);
						}
						else 
						{
							return new Nothing<Pair<T,String>>();
						}
					}
				},
				run0
				);
		Parsec<T> p = new Parsec<T>(run1);
		return p;
	}
	
	public static <T> Parsec<T> mzero() // we need <T> after static otherwise Java can't infer it is the same T as the class level
	{ // failing parsec
		Parsec<T> p = new Parsec<T>(
				new Function<Maybe<Pair<T,String>>,String>()
				{
					public Maybe<Pair<T,String>> apply(String tokens)
					{
						return new Nothing<Pair<T,String>>();
					}
					
				});
		return p; 
	}
	
	public static Parsec<String> item()
	{ // parse one item
		Parsec<String> p = new Parsec<String>(
				new Function<Maybe<Pair<String,String>>,String>()
				{
					public Maybe<Pair<String,String>> apply(String tokens)
					{
						if (tokens.length() == 0)
						{
							return new Nothing<Pair<String,String>>();
						}
						else
						{
							return new Just<Pair<String,String>>(new Pair<String,String>(tokens.substring(0,1),tokens.substring(1)));
						}
					}
				}
				);
		return p;
	}
	
	
	public static Parsec<String> sat(final Function<Boolean,String> condition)
	{ // parse if it satisfies the condition
		Parsec<String> p = new Parsec<String>(
				new Function<Maybe<Pair<String,String>>,String>()
				{
					public Maybe<Pair<String,String>> apply(String tokens)
					{
						Maybe<Pair<String,String>> mb = item().run.apply(tokens);
						if (mb.isJust())
						{
							Just<Pair<String,String>> j = (Just<Pair<String,String>>) mb;
							String c = j.result.fst;
							// String cs = j.result.snd;
							if (condition.apply(c))
							{
								return mb;
							}
							else
							{
								return new Nothing<Pair<String,String>>();
							}
						}
						else 
						{
							return new Nothing<Pair<String,String>>();
						}
					}
				});
		return p;
	}
	
	public static <T> Parsec<T> choice(final Parsec<T> p1, final Parsec<T> p2)
	{ // choice parser
		Parsec<T> p = new Parsec<T>(
				new Function<Maybe<Pair<T,String>>,String>()
				{
					public Maybe<Pair<T,String>> apply(String tokens)
					{
						Maybe<Pair<T,String>> mb = p1.run.apply(tokens);
						if (mb.isJust())
						{
							return mb;
						}
						else
						{
							return p2.run.apply(tokens);
						}
					}
				});
		return p;
		
	}
	
	
	public static <T> Parsec<List<T>> repeat(final Parsec<T> p)
	{ // repeat parser
		Parsec<List<T>> q = new Parsec<List<T>>(
				new Function<Maybe<Pair<List<T>,String>>,String>()
				{
					public Maybe<Pair<List<T>,String>> apply(String tokens)
					{
						List<T> l = new ArrayList<T>();
						String remain = tokens;
						Maybe<Pair<T,String>> mb = p.run.apply(remain);
						while (mb.isJust())
						{
							Just<Pair<T,String>> j = (Just<Pair<T,String>>)mb;
							l.add(j.result.fst);
							remain = j.result.snd;
							mb = p.run.apply(remain);
						}
						return new Just<Pair<List<T>,String>>(new Pair<List<T>,String>(l,remain));
					}
				});
		return q;
	}
	
	public static void main(String[] args)
	{
		// Parsec<String> p = mzero();
		Parsec<String> p1 = item();
		Parsec<String> p2 = 
			sat(new Function<Boolean,String>()
					{ 
						public Boolean apply(String tokens)
						{
							// System.out.println(tokens);
							return tokens.equals("h");
						}
					});
		Parsec<List<String>> p3 = repeat(choice(p2,p1));
		Maybe<Pair<List<String>,String>> mb = p3.run.apply("hello");
		if (mb.isJust())
		{
			Just<Pair<List<String>, String>> j = (Just<Pair<List<String>,String>>) mb;
			System.out.println(j.result.fst + ":" + j.result.snd);
		} 
		else
		{
			System.out.println("failed");
		}
	}

}
