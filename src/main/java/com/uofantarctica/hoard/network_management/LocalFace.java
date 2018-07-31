package com.uofantarctica.hoard.network_management;

import com.uofantarctica.hoard.message_passing.event.ExpressIncrementingInterest;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.event.PutData;
import com.uofantarctica.hoard.message_passing.event.RegisterPrefix;
import com.uofantarctica.hoard.message_passing.event.SendEncoding;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.ForwardingFlags;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.OnRegisterSuccess;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.data_management.DataHoarder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//TODO challenge to overcome: if the face loses the connection, then whatever interests we had outstanding will be
// dropped on the floor. This means we have to keep track of the outstanding interests. And re-express them when we
// reconnect.
public class LocalFace {
	private static final Logger log = LoggerFactory.getLogger(LocalFace.class);
	private Face face;
	private final Set<RegisterPrefix> prefixRegistrations = new HashSet<>();
	private final Set<ExpressInterest> outboundInterest = new HashSet<>();
	private final ExponentialBackoff retryFacePolicy = new ExponentialBackoff(10, 120000, Integer.MAX_VALUE);
	private boolean isRetrying = false;
	private final List<NdnEvent> eventsToRetry = new ArrayList<>();

	public LocalFace(Face face) {
		this.face = face;
	}

	public LocalFace() {
		retryInit();
	}

	private void retryInit() {
		if (isRetrying) {
			throw new AlreadyRetryingException();
		}
		else {
			isRetrying = true;
			retryFacePolicy.begin();
			int attempts = 1;
			do {
				++attempts;
				try {
					log.debug("Retrying face init");
					face = FaceInit.getRawFace();
					retryNdnEvents();
					break;
				} catch (IOException e) {
					log.error("Failed in retrying face", e);
				}
				catch (AlreadyRetryingException e) {
					log.error("Failed at retrying, while retrying, loop again", e);
				}
				log.debug("Retrying face init, attempt: {}", attempts);
			} while(retryFacePolicy.sleepingRetry());
			isRetrying = false;
		}
	}

	private void retryNdnEvents() {
		for (NdnEvent ndnEvent : eventsToRetry) {
			ndnEvent.fire(this);
		}
		for (ExpressInterest expressInterest : outboundInterest) {
			expressInterest.fire(this);
		}
		for (RegisterPrefix prefixRegistration : prefixRegistrations) {
			prefixRegistration.fire(this);
		}
		eventsToRetry.clear();
	}

	public void expressInterest(Interest interest, DataHoarder hoarder) {
		try {
			HoardTracker tracker = new HoardTracker(this, interest, hoarder);
			face.expressInterest(interest, tracker, tracker, tracker);
		} catch (IOException e) {
			log.error("Error expressInterest: {}", interest.getName().toUri(), e);
			eventsToRetry.add(new ExpressIncrementingInterest(interest, hoarder));
			retryInit();
		}
	}

	public void putData(Data data) {
		try {
			face.putData(data);
		} catch (IOException e) {
			log.error("Error putData: {}", data.getName().toUri(), e);
			eventsToRetry.add(new PutData(data));
			retryInit();
		}
	}

	public void processEvents() {
		try {
			face.processEvents();
		} catch (IOException e) {
			log.error("Failed to processEvents, IOException, retrying init",e);
			retryInit();
		} catch (Exception e) {
			log.error("Failed to processEvents",e);
		}
	}

	public void registerPrefix(Name prefix, OnInterestCallback onInterestCallback, OnRegisterFailed onRegisterFailed, OnRegisterSuccess onRegisterSuccess, ForwardingFlags flags, WireFormat wireFormat) {
		try {
			prefixRegistrations.add(new RegisterPrefix(prefix, onInterestCallback, onRegisterFailed, onRegisterSuccess, flags, wireFormat));
			face.registerPrefix(prefix, onInterestCallback, onRegisterFailed, onRegisterSuccess, flags, wireFormat);
			log.debug("registerFlatDataPrefix: {}", prefix.toUri());
		} catch (IOException e) {
			log.error("Error registerFlatDataPrefix: {}, IOException, retryingInit", prefix.toUri(), e);
			retryInit();
		} catch (Exception e) {
			log.error("Error registerFlatDataPrefix: {}", prefix.toUri(), e);
		}
	}

	public void send(Blob dataEncoding) {
		try {
			face.send(dataEncoding);
			eventsToRetry.add(new SendEncoding(dataEncoding));
		} catch (IOException e) {
			log.error("Error send, IOException, retryingInit", e);
			retryInit();
		}
	}

	public void callLater(double delay, Runnable action) {
		face.callLater(delay, action);
	}

	public void trackOutboundInterest(Interest interest, DataHoarder hoarder) {
		outboundInterest.add(new ExpressInterest(interest, hoarder));
	}

	public void markInterestInbound(Interest interest, DataHoarder hoarder) {
		outboundInterest.remove(new ExpressInterest(interest, hoarder));
	}
}
