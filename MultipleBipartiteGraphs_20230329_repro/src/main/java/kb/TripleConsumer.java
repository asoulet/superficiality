package kb;

public interface TripleConsumer {
	public void begin();
	public void end();
	public void consume(String s, String p, String o);
}
