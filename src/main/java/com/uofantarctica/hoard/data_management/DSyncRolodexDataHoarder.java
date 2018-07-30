package com.uofantarctica.hoard.data_management;

import com.uofantarctica.dsync.model.Rolodex;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.network_management.ExponentialInterestBackoff;

public class DSyncRolodexDataHoarder extends com.uofantarctica.hoard.data_management.DataHoarder {
	private final Rolodex rolodex;
	private final String broadcastPrefix;
	private final com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder;

	public DSyncRolodexDataHoarder(String broadcastPrefix,
			com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder,
			ExponentialInterestBackoff retryPolicy ) {
		super(syncDataHoarder.getNdnEvents(), syncDataHoarder.getNdnTraffic(), retryPolicy);
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
						retryPolicy .duplicate()));
	}

	@Override
	protected void maxedOutRetries(Interest interest) {
		expressNewInterest(interest);
	}
}
