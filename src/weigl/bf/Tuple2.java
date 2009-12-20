package weigl.bf;

public class Tuple2<T, V> {
	private final T t;
	private final V v;

	public Tuple2(T t, V v) {
		this.t = t;
		this.v = v;
	}

	public static <T, V> Tuple2<T, V> create(T t, V v) {
		return new Tuple2<T, V>(t, v);
	}

	public T get1() {
		return t;
	}

	public V get2() {
		return v;
	}
}
