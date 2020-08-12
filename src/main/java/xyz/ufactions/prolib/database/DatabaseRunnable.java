package xyz.ufactions.prolib.database;

public class DatabaseRunnable
{
	private final Runnable _runnable;
	private int _failedAttempts = 0;
	
	public DatabaseRunnable(Runnable runnable)
	{
		_runnable = runnable;
	}
	
	public void run()
	{
		_runnable.run();
	}
	
	public void incrementFailCount()
	{
		_failedAttempts++;
	}
	
	public int getFailedCounts()
	{
		return _failedAttempts;
	}
}
