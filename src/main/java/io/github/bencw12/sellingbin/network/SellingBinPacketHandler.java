package io.github.bencw12.sellingbin.network;

import io.github.bencw12.sellingbin.SellingBin;
import io.github.bencw12.sellingbin.network.packet.SellingBinOffersPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class SellingBinPacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SellingBin.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {
        CHANNEL.registerMessage(index++, SellingBinOffersPacket.class,
                SellingBinOffersPacket::encode, SellingBinOffersPacket::decode,
                SellingBinOffersPacket::handle);
    }
}
