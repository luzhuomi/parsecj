package parsec;

// Higher order function interface

public interface Function<R,A> { // function of type A -> R
	public R apply(A a); 
}
