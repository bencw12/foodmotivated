package io.github.bencw12.foodmotivated.data;

import io.github.bencw12.foodmotivated.network.FoodMotivatedPacketHandler;
import io.github.bencw12.foodmotivated.network.packet.SellingBinOffersPacket;
import io.github.bencw12.foodmotivated.world.item.SellingBinOffers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class SellingBinDataManager {
    private static final String OFFERS_KEY = "selling_bin_offers";

    public static SellingBinOffersSavedData getOffers(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(SellingBinOffersSavedData::load, SellingBinOffersSavedData::new, OFFERS_KEY);
    }

    public static void sendOffersToClient(ServerPlayer player) {
        SellingBinOffersPacket pkt = new SellingBinOffersPacket(SellingBinOffers.OFFERS);
        FoodMotivatedPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), pkt);
    }
}
