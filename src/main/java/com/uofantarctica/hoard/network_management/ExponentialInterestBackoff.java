package com.uofantarctica.hoard.network_management;

import com.netflix.astyanax.retry.BoundedExponentialBackoff;
import com.netflix.astyanax.retry.SleepingRetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class ExponentialInterestBackoff extends BoundedExponentialBackoff {
	private static final Logger log = LoggerFactory.getLogger(ExponentialInterestBackoff.class);

	private long baseSleepTimeMs;
	private long maxSleepTimeMs;
	private int max;

	public ExponentialInterestBackoff(long baseSleepTimeMs, long maxSleepTimeMs, int max) {
		super(baseSleepTimeMs, maxSleepTimeMs, max);
		this.baseSleepTimeMs = baseSleepTimeMs;
		this.maxSleepTimeMs = maxSleepTimeMs;
		this.max = max;
	}

	public long getInterestLifetime() {
		return super.getSleepTimeMs();
	}

	private Class getSleepingRetryClass() throws Exception {
		return this.getClass().getSuperclass().getSuperclass().getSuperclass();
	}

	public int getAttempts() throws Exception {
		Field f = getAttemptsField();
		f.setAccessible(true);
		return (int)f.get(getSleepingRetryInstance());
	}

	private SleepingRetryPolicy getSleepingRetryInstance() throws Exception {
		return this;
	}

	private Field getAttemptsField() throws Exception {
		Field f = getSleepingRetryClass().getDeclaredField("attempts");
		f.setAccessible(true);
		return f;
	}

	public boolean incAttempts() {
		try {
			Field f = getAttemptsField();
			f.setAccessible(true);
			int attempts = (int) f.get(getSleepingRetryInstance());
			++attempts;
			f.set(getSleepingRetryInstance(), attempts);
			return true;
		}
		catch (Exception e) {
			log.error("failed to access private member variable, to increment attempts in ExponentialInterestBackoff",
					e);
			return false;
		}
	}

	public boolean allowRetryBlocking() {
		return allowRetry();
	}

	public boolean allowRetryNonBlocking() {
		try {
			return max == -1 || getAttempts() < max;
		}
		catch (Exception e) {
			log.error("Not allowing retry because could not acces private member variable attempts", e);
			return false;
		}
	}


	@Override
	public ExponentialInterestBackoff duplicate() {
		return new ExponentialInterestBackoff(baseSleepTimeMs, maxSleepTimeMs, max);
	}
}
