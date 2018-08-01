package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.DataHoarder;
import com.uofantarctica.hoard.network_management.LocalFace;

//TODO this class might be unnecessary...
public class ExpressIncrementingInterest extends ExpressInterest {

    public ExpressIncrementingInterest(Name n, DataHoarder hoarder) {
    	super(new Interest(n), hoarder);
    }

	public ExpressIncrementingInterest(Interest i, DataHoarder hoarder) {
    	super(i, hoarder);
	}

	@Override
    public void fire(LocalFace face) {
	    face.expressInterest(this);
    }

	@Override
	public String getUniqueName() {
		return SimpleExpressInterest.class.getSimpleName() + interest.getName().toUri();
	}

	@Override
	public String toString() {
		return "ExpressIncrementingInterest{" +
				"interest=" + interest.toUri() +
				", hoarder=" + hoarder +
				'}';
	}
}
