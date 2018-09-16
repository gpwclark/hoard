package com.uofantarctica.hoard;

import com.uofantarctica.dsync.model.ReturnStrategy;

public final class HoardBuilder {
	private String theDataPrefix;
	private String theBroadcastPrefix;
	private String chatRoom;
	private String screenName;

	private HoardBuilder() {
	}

	public static HoardBuilder aHoard() {
		return new HoardBuilder();
	}

	public HoardBuilder withTheDataPrefix(String theDataPrefix) {
		this.theDataPrefix = theDataPrefix;
		return this;
	}

	public HoardBuilder withTheBroadcastPrefix(String theBroadcastPrefix) {
		this.theBroadcastPrefix = theBroadcastPrefix;
		return this;
	}

	public HoardBuilder withChatRoom(String chatRoom) {
		this.chatRoom = chatRoom;
		return this;
	}

	public HoardBuilder withScreenName(String screenName) {
		this.screenName = screenName;
		return this;
	}

	public Hoard build() {
		return new Hoard(theDataPrefix, theBroadcastPrefix, chatRoom, screenName);
	}
}
