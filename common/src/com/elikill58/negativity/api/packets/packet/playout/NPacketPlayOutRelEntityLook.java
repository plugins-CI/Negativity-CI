package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutRelEntityLook extends NPacketPlayOutEntity {

	@Override
	public void read(PacketSerializer serializer, Version version) {
		super.read(serializer, version);
		this.yaw = serializer.readByte();
		this.pitch = serializer.readByte();
		this.isGround = serializer.readUnsignedByte() != 0;
	}

	@Override
	public PacketType getPacketType() {
		return Server.REL_ENTITY_LOOK;
	}
}
