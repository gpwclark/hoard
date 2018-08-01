package com.uofantarctica.hoard.data_management;

import com.uofantarctica.dsync.model.Rolodex;
import com.uofantarctica.hoard.network_management.ExponentialBackoff;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

public class DSyncRolodexDataHoarder extends com.uofantarctica.hoard.data_management.DataHoarder {
	private final Rolodex rolodex;
	private final String broadcastPrefix;
	private final com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder;

	public DSyncRolodexDataHoarder(String broadcastPrefix,
			com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder,
			ExponentialBackoff syncDataRetryPolicy) {
		super(syncDataHoarder.getNdnEvents(), syncDataHoarder.getNdnTraffic(), syncDataRetryPolicy);
		this.rolodex = new Rolodex("hoard", "rolodex");
		this.syncDataHoarder = syncDataHoarder;
		this.broadcastPrefix = broadcastPrefix;
	}

	@Override
	protected void processData(Interest interest, Data data) {
		syncDataHoarder.processData(interest, data);
		expressNewInterest(newRolodexInterest(data));
	}

	private Interest newRolodexInterest(Data data) {
		rolodex.mergeContacts(data);
		String hash = rolodex.getRolodexHashString();
		Name name = new Name(broadcastPrefix)
				.append(hash);
		return new Interest(name);
	}

	private void expressNewInterest(Interest interest) {
		expressInterest(interest,
				new DSyncRolodexDataHoarder(broadcastPrefix,
						syncDataHoarder,
						retryPolicy.duplicate()));
	}

	@Override
	protected void maxedOutRetries(Interest interest) {
		expressNewInterest(interest);
	}
}
