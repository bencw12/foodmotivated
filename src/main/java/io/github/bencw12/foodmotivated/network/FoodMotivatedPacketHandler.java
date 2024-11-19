package io.github.bencw12.foodmotivated.network;

import io.github.bencw12.foodmotivated.FoodMotivated;
import io.github.bencw12.foodmotivated.network.packet.SellingBinOffersPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class FoodMotivatedPacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FoodMotivated.MOD_ID, "main"),
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
