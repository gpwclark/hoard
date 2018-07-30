/**
 * Copyright (C) 2014-2018 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * A copy of the GNU Lesser General Public License is in the file COPYING.
 */

package com.uofantarctica.hoard.data_management;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.ForwardingFlags;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.OnRegisterSuccess;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.traffic.FlatInterestTraffic;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.message_passing.event.RegisterPrefix;
import com.uofantarctica.hoard.message_passing.event.SendEncoding;
import com.uofantarctica.hoard.network_management.InterestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A MemoryContentCache holds a set of Data packets and answers an Interest to
 * return the correct Data packet. The cache is periodically cleaned up to
 * remove each stale Data packet based on its FreshnessPeriod (if it has one).
 * @note This class is an experimental feature.  See the API docs for more detail at
 * http://named-data.net/doc/ndn-ccl-api/memory-content-cache.html .
 */
public class MemoryContentCache implements OnInterestCallback {
	private static final Logger log = LoggerFactory.getLogger(MemoryContentCache.class);
	private final Enqueue<NdnEvent> ndnEvents;
	private final Enqueue<NdnTraffic> ndnTraffic;

	private final double cleanupIntervalMilliseconds_;
	private double nextCleanupTime_;
	private final HashMap onDataNotFoundForPrefix_ = new HashMap();
	private final ArrayList<Content> noStaleTimeCache_ = new ArrayList<>();
	private final ArrayList<StaleTimeContent> staleTimeCache_ =
			new ArrayList<>();
	private final Name.Component emptyComponent_ = new Name.Component();
	private final ArrayList<PendingInterest> pendingInterestTable_ = new ArrayList<>();
	private OnInterestCallback storePendingInterestCallback_;
	private double minimumCacheLifetime_ = 0;
	Set<String> pendingInterests = new HashSet<>();

	public MemoryContentCache(Enqueue<NdnEvent> ndnEvents, Enqueue<NdnTraffic> ndnTraffic, double cleanupIntervalMilliseconds) {
		cleanupIntervalMilliseconds_ = cleanupIntervalMilliseconds;
		this.ndnEvents = ndnEvents;
		this.ndnTraffic = ndnTraffic;
		construct();
	}

	public MemoryContentCache(Enqueue<NdnEvent> ndnEvents, Enqueue<NdnTraffic> ndnTraffic)
	{
		cleanupIntervalMilliseconds_ = 1000.0;
		this.ndnEvents = ndnEvents;
		this.ndnTraffic = ndnTraffic;
		construct();
	}

	private void
	construct()
	{
		nextCleanupTime_ = System.currentTimeMillis() + cleanupIntervalMilliseconds_;

		storePendingInterestCallback_ = new OnInterestCallback() {
			public void onInterest
					(Name localPrefix, Interest localInterest, Face localFace,
					 long localInterestFilterId, InterestFilter localFilter)
			{
				storePendingInterest(localInterest);
			}
		};
	}

	private final void registerFlatDataPrefix(Name prefix, OnRegisterFailed onRegisterFailed,
											  OnRegisterSuccess onRegisterSuccess, OnInterestCallback onDataNotFound,
											  ForwardingFlags flags, WireFormat wireFormat) {
		if (onDataNotFound != null) {
			onDataNotFoundForPrefix_.put(prefix.toUri(), onDataNotFound);
		}
		//TODO keep track of registeredPrefixIdToUnregisterLater
		//registeredPrefixIdList_.add(registeredPrefixId);
		//long registeredPrefixId = face_.registerFlatDataPrefix (prefix, this, onRegisterFailed, onRegisterSuccess, flags, wireFormat);
		ndnEvents.enQ(new RegisterPrefix(prefix, this, onRegisterFailed, onRegisterSuccess));
	}

	public final void registerFlatDataPrefix(Name prefix, InterestListener interestListener) {
		registerFlatDataPrefix(prefix, interestListener, interestListener, interestListener, new ForwardingFlags(), WireFormat.getDefaultWireFormat());
	}

  /*TODO move out of here when we handle registeredPrefixIds
  public final void
  unregisterAll()
  {
    for (int i = 0; i < registeredPrefixIdList_.size(); ++i)
  //face_.removeRegisteredPrefix( );
    ndnEvents.enQ(new RemoveRegisteredPrefix((long)registeredPrefixIdList_.get(i)));consume aarin nor
    registeredPrefixIdList_.clear();

    // Also clear each onDataNotFoundForPrefix given to registerFlatDataPrefix.
    onDataNotFoundForPrefix_.clear();
  }
  */

	/**
	 * Add the Data packet to the cache so that it is available to use to
	 * answer interests. If data.getMetaInfo().getFreshnessPeriod() is not
	 * negative, set the staleness time to now plus the maximum of
	 * data.getMetaInfo().getFreshnessPeriod() and minimumCacheLifetime, which is
	 * checked during cleanup to remove stale content.
	 * This also checks if cleanupIntervalMilliseconds
	 * milliseconds have passed and removes stale content from the cache. After
	 * removing stale content, remove timed-out pending interests from
	 * storePendingInterest(), then if the added Data packet satisfies any
	 * interest, send it through the face and remove the interest from the pending
	 * interest table.
	 * @param data The Data packet object to put in the cache. This copies the
	 * fields from the object.
	 */
	public final void
	add(Data data)
	{
		double nowMilliseconds = System.currentTimeMillis();
		doCleanup(nowMilliseconds);

		if (data.getMetaInfo().getFreshnessPeriod() >= 0.0) {
			// The content will go stale, so use staleTimeCache_.
			StaleTimeContent content = new StaleTimeContent
					(data, nowMilliseconds, minimumCacheLifetime_);
			// Insert into staleTimeCache, sorted on content.cacheRemovalTimeMilliseconds_.
			// Search from the back since we expect it to go there.
			int i = staleTimeCache_.size() - 1;
			while (i >= 0) {
				if (staleTimeCache_.get(i).getCacheRemovalTimeMilliseconds() <=
						content.getCacheRemovalTimeMilliseconds())
					break;
				--i;
			}
			// Element i is the greatest less than or equal to
			// content.cacheRemovalTimeMilliseconds_, so insert after it.
			staleTimeCache_.add(i + 1, content);
		}
		else
			// The data does not go stale, so use noStaleTimeCache_.
			noStaleTimeCache_.add(new Content(data));

		// Remove timed-out interests and check if the data packet matches any
		// pending interest.
		// Go backwards through the list so we can erase entries.
		for (int i = pendingInterestTable_.size() - 1; i >= 0; --i) {
			PendingInterest pendingInterest = pendingInterestTable_.get(i);
			if (pendingInterest.isTimedOut(nowMilliseconds)) {
			    removePendingInterest(i);
				continue;
			}

			//Must try to check matchesData for exclude filters.
			boolean matchesData = isDataMatch(data, new Interest(data.getName()));
			if (matchesData
                && pendingInterest.getInterest().matchesName(data.getName())) {
				// Send to the same face from the original call to onInterest.
				// wireEncode returns the cached encoding if available.
				//pendingInterest.getFace().send(data.wireEncode());
				ndnEvents.enQ(new SendEncoding(data.wireEncode()));

				// The pending interest is satisfied, so remove it.
				removePendingInterest(i);
			}
		}
	}

	private void removePendingInterest(int i) {
		PendingInterest pendingInterest = pendingInterestTable_.remove(i);
		pendingInterests.remove(pendingInterest.getInterest().getName().toUri());
	}

	/**
	 * Store an interest from an OnInterest callback in the internal pending
	 * interest table (normally because there is no Data packet available yet to
	 * satisfy the interest). add(data) will check if the added Data packet
	 * satisfies any pending interest and send it through the face.
	 * @param interest The Interest for which we don't have a Data packet yet. You
	 * should not modify the interest after calling this.
	 * comes from the OnInterest callback.
	 */
	public final void
	storePendingInterest(Interest interest) {
	    if (isNotPending(interest)) {
	    	addToPendingInterests(interest);
		}
	}

	private void addToPendingInterests(Interest interest) {
		pendingInterestTable_.add(new PendingInterest(interest));
		pendingInterests.add(interest.getName().toUri());
	}

	public final boolean isNotPending(Interest interest) {
	    return !pendingInterests.contains(interest.getName().toUri());
	}

	/**
	 * Dequeue the minimum lifetime before removing stale content from the cache.
	 * @return The minimum cache lifetime in milliseconds.
	 */
	public final double
	getMinimumCacheLifetime() { return minimumCacheLifetime_; }

	//TODO if this is not an in memory cache we want to set cache lifetime reallllly high.
	/**
	 * Set the minimum lifetime before removing stale content from the cache which
	 * can keep content in the cache longer than the lifetime defined in the meta
	 * info. This can be useful for matching interests where MustBeFresh is false.
	 * The default minimum cache lifetime is zero, meaning that content is removed
	 * when its lifetime expires.
	 * @param minimumCacheLifetime The minimum cache lifetime in milliseconds.
	 */
	public final void
	setMinimumCacheLifetime(double minimumCacheLifetime)
	{
		minimumCacheLifetime_ = minimumCacheLifetime;
	}

	@Override
	public final void onInterest (Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		ndnTraffic.enQ(new FlatInterestTraffic(prefix, interest, face, interestFilterId, filter));
	}

	public final void processInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
	    List<NdnEvent> events = getEvents(prefix, interest, face, interestFilterId, filter);
		for (NdnEvent event : events) {
			ndnEvents.enQ(event);
		}
    }

	private final List<NdnEvent> getEvents(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		List<NdnEvent> events = new ArrayList<>();
		double nowMilliseconds = System.currentTimeMillis();
		doCleanup(nowMilliseconds);

		Name.Component selectedComponent = null;
		Blob selectedEncoding = null;
		// We need to iterate over both arrays.
		int totalSize = staleTimeCache_.size() + noStaleTimeCache_.size();
		for (int i = 0; i < totalSize; ++i) {
			Content content;
			boolean isFresh = true;
			if (i < staleTimeCache_.size()) {
				StaleTimeContent staleTimeContent = staleTimeCache_.get(i);
				content = staleTimeContent;
				isFresh = staleTimeContent.isFresh(nowMilliseconds);
			}
			else {
				// We have iterated over the first array. Dequeue from the second.
				content = noStaleTimeCache_.get(i - staleTimeCache_.size());
			}

			//Must try to check matchesData for exclude filters.
			boolean matchesData = isDataMatch(content.getData(), interest);
			if (matchesData
				&& interest.matchesName(content.getName())
				&& !(interest.getMustBeFresh()
				&& !isFresh)) {
				if (interest.getChildSelector() < 0) {
					// No child selector, so send the first match that we have found.
					events.add(new SendEncoding(content.getDataEncoding()));
					return events;
				}
				else {
					// Update selectedEncoding based on the child selector.
					Name.Component component;
					if (content.getName().size() > interest.getName().size())
						component = content.getName().get(interest.getName().size());
					else
						component = emptyComponent_;

					boolean gotBetterMatch = false;
					if (selectedEncoding == null)
						// Save the first match.
						gotBetterMatch = true;
					else {
						if (interest.getChildSelector() == 0) {
							// Leftmost child.
							if (component.compare(selectedComponent) < 0)
								gotBetterMatch = true;
						}
						else {
							// Rightmost child.
							if (component.compare(selectedComponent) > 0)
								gotBetterMatch = true;
						}
					}

					if (gotBetterMatch) {
						selectedComponent = component;
						selectedEncoding = content.getDataEncoding();
					}
				}
			}
		}

		if (selectedEncoding != null) {
			// We found the leftmost or rightmost child.
			events.add(new SendEncoding(selectedEncoding));
		}
		else {
			// Call the onDataNotFound callback (if defined).
			Object onDataNotFound = onDataNotFoundForPrefix_.get(prefix.toUri());
			if (onDataNotFound != null) {
				try {
					//TODO is this right?
					((OnInterestCallback)onDataNotFound).onInterest(prefix, interest, face, interestFilterId, filter);
				} catch (Exception e) {
					log.error("Error in onDataNotFound", e);
				}
			}
		}
		return events;
	}

	private boolean isDataMatch(Data data, Interest interest) {
		boolean matchesData = true;
		try {
			matchesData = interest.matchesData(data);
		} catch (EncodingException e) {
			log.error("Failted to check exclude filters on interest: {}", interest.getName().toUri(), e);
		}
		return matchesData;
	}

	/**
	 * Content is a private class to hold the name and encoding for each entry
	 * in the cache. This base class is for a Data packet without a
	 * FreshnessPeriod.
	 */
	private class Content {
		/**
		 * Create a new Content entry to hold data's name and wire encoding.
		 * @param data The Data packet whose name and wire encoding are copied.
		 */
		public Content(Data data)
		{
			// wireEncode returns the cached encoding if available.
			name_ = data.getName();
			dataEncoding_ = data.wireEncode();
		}

		public final Name
		getName() { return name_; }

		public final Blob
		getDataEncoding() { return dataEncoding_; }

		private final Name name_;
		private final Blob dataEncoding_;

		public Data getData() {
			return new Data(name_)
					.setContent(dataEncoding_);
		}
	}

	/**
	 * StaleTimeContent extends Content to include the cacheRemovalTimeMilliseconds_
	 * for when this entry should be cleaned up from the cache.
	 */
	private class StaleTimeContent extends Content {
		/**
		 * Create a new StaleTimeContent to hold data's name and wire encoding
		 * as well as the cacheRemovalTimeMilliseconds_ which is now plus the maximum of
		 * data.getMetaInfo().getFreshnessPeriod() and the minimumCacheLifetime.
		 * @param data The Data packet whose name and wire encoding are copied.
		 * @param nowMilliseconds The current time in milliseconds from
		 * Common.getNowMilliseconds().
		 * @param minimumCacheLifetime The minimum cache lifetime in milliseconds.
		 */
		public StaleTimeContent
		(Data data, double nowMilliseconds, double minimumCacheLifetime)
		{
			// wireEncode returns the cached encoding if available.
			super(data);

			cacheRemovalTimeMilliseconds_ = nowMilliseconds +
					Math.max(data.getMetaInfo().getFreshnessPeriod(), minimumCacheLifetime);
			freshnessExpiryTimeMilliseconds_ = nowMilliseconds +
					data.getMetaInfo().getFreshnessPeriod();
		}

		/**
		 * Check if this content is stale and should be removed from the cache,
		 * according to the content freshness period and the minimumCacheLifetime.
		 * @param nowMilliseconds The current time in milliseconds from
		 * Common.getNowMilliseconds().
		 * @return True if this content should be removed, otherwise false.
		 */
		public final boolean
		isPastRemovalTime(double nowMilliseconds)
		{
			return cacheRemovalTimeMilliseconds_ <= nowMilliseconds;
		}

		/**
		 * Check if the content is still fresh according to its freshness period
		 * (independent of when to remove from the cache).
		 * @param nowMilliseconds The current time in milliseconds from
		 * Common.getNowMilliseconds().
		 * @return True if the content is still fresh, otherwise false.
		 */
		public final boolean
		isFresh(double nowMilliseconds)
		{
			return freshnessExpiryTimeMilliseconds_ > nowMilliseconds;
		}

		public final double
		getCacheRemovalTimeMilliseconds() { return cacheRemovalTimeMilliseconds_; }

		private final double cacheRemovalTimeMilliseconds_; /**< The time when the content
		 becomes stale and should be removed from the cache in milliseconds
		 according to Common.getNowMilliseconds(). */
		private final double freshnessExpiryTimeMilliseconds_; /**< The time when
		 the freshness period of the content expires (independent of when to
		 remove from the cache) in milliseconds according to Common.getNowMilliseconds(). */
	}

	/**
	 * A PendingInterest holds an interest which onInterest received but could
	 * not satisfy. When we add a new data packet to the cache, we will also check
	 * if it satisfies a pending interest.
	 */
	private static class PendingInterest {
		/**
		 * Create a new PendingInterest and set the timeoutTime_ based on the current
		 * time and the interest lifetime.
		 * @param interest The interest.
		 */
		public PendingInterest(Interest interest) {
			interest_ = interest;

			// Set up timeoutTimeMilliseconds_.
			if (interest_.getInterestLifetimeMilliseconds() >= 0.0)
				timeoutTimeMilliseconds_ = System.currentTimeMillis() +
						interest_.getInterestLifetimeMilliseconds();
			else
				// No timeout.
				timeoutTimeMilliseconds_ = -1.0;
		}

		/**
		 * Return the interest given to the constructor.
		 */
		public final Interest
		getInterest() { return interest_; }

		/**
		 * Check if this interest is timed out.
		 * @param nowMilliseconds The current time in milliseconds from
		 *   Common.getNowMilliseconds.
		 * @return True if this interest timed out, otherwise false.
		 */
		public final boolean
		isTimedOut(double nowMilliseconds)
		{
			return timeoutTimeMilliseconds_ >= 0.0 && nowMilliseconds >= timeoutTimeMilliseconds_;
		}

		private final Interest interest_;
		private final double timeoutTimeMilliseconds_; /**< The time when the
		 * interest times out in milliseconds according to ndn_getNowMilliseconds,
		 * or -1 for no timeout. */
	}

	/**
	 * Check if now is greater than nextCleanupTime_ and, if so, remove stale
	 * content from staleTimeCache_ and reset nextCleanupTime_ based on
	 * cleanupIntervalMilliseconds_. Since add(Data) does a sorted insert into
	 * staleTimeCache_, the check for stale data is quick and does not require
	 * searching the entire staleTimeCache_.
	 * @param nowMilliseconds The current time in milliseconds from
	 * Common.getNowMilliseconds().
	 */
	private void
	doCleanup(double nowMilliseconds)
	{
		if (nowMilliseconds >= nextCleanupTime_) {
			// staleTimeCache_ is sorted on cacheRemovalTimeMilliseconds_, so we only need to
			// erase the stale entries at the front, then quit.
			while (staleTimeCache_.size() > 0 &&
					staleTimeCache_.get(0).isPastRemovalTime(nowMilliseconds))
				staleTimeCache_.remove(0);

			nextCleanupTime_ = nowMilliseconds + cleanupIntervalMilliseconds_;
		}
	}
}
