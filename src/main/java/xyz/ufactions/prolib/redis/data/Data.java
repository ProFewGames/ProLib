package xyz.ufactions.prolib.redis.data;


public interface Data
{
	/**
	 * @return the unique id key representing this {@link Data} object in {@link DataRepository}s.
	 */
	public String getDataId();
}
