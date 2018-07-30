package com.uofantarctica.hoard.protocols;

public abstract class SyncStateProtoPacket implements SyncPacket{
    protected com.uofantarctica.hoard.protocols.SyncStateProto.SyncStateMsg syncStateMsg;

    public SyncStateProtoPacket(com.uofantarctica.hoard.protocols.SyncStateProto.SyncStateMsg syncStateMsg) {
        this.syncStateMsg = syncStateMsg;
    }

    public int getCount() {
        return syncStateMsg.getSsCount();
    }

    public com.uofantarctica.hoard.protocols.SyncStateProto.SyncState get(int i) {
        return syncStateMsg.getSs(i);
    }
}
