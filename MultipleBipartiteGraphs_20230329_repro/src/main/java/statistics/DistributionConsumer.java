package statistics;

public interface DistributionConsumer {

	public void begin();
	public void add(int k, int count);
	public void end();
}
