package com.uofantarctica.hoard.protocols;

public abstract class SyncStateProtoPacket implements SyncPacket{
    protected com.uofantarctica.jndn.proto.SyncStateProto.SyncStateMsg syncStateMsg;

    public SyncStateProtoPacket(com.uofantarctica.jndn.proto.SyncStateProto.SyncStateMsg syncStateMsg) {
        this.syncStateMsg = syncStateMsg;
    }

    public int getCount() {
        return syncStateMsg.getSsCount();
    }

    public com.uofantarctica.jndn.proto.SyncStateProto.SyncState get(int i) {
        return syncStateMsg.getSs(i);
    }
}
