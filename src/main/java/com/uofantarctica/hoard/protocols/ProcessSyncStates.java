package com.uofantarctica.hoard.protocols;

import com.uofantarctica.jndn.proto.SyncStateProto;
public class ProcessSyncStates {
    public static SyncPacket build(SyncStateProto.SyncStateMsg syncStateMsg) {
        SyncPacket packet = new NoOpPacket();
        if (syncStateMsg.hasProtocol()) {
            switch (syncStateMsg.getProtocol()) {
                case "DSYNC": packet = new com.uofantarctica.hoard.protocols.DSyncPacket(syncStateMsg);
                    break;
                case "CHRONOSYNC": packet = new com.uofantarctica.hoard.protocols.ChronoSyncPacket(syncStateMsg);
                    break;
                default: packet = new NoOpPacket();
                    break;
            }
        }
        return packet;
    }
}
