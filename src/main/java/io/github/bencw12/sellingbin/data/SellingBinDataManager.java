package io.github.bencw12.sellingbin.data;

import io.github.bencw12.sellingbin.network.SellingBinPacketHandler;
import io.github.bencw12.sellingbin.network.packet.SellingBinPricesPacket;
import io.github.bencw12.sellingbin.world.item.SellingBinPrices;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class SellingBinDataManager {
    private static final String PRICES_KEY = "selling_bin_prices";

    public static SellingBinPricesSavedData getPrices(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(SellingBinPricesSavedData::load, SellingBinPricesSavedData::new, PRICES_KEY);
    }

    public static void sendPricesToClient(ServerPlayer player) {
        SellingBinPricesPacket pkt = new SellingBinPricesPacket(SellingBinPrices.PRICES);
        SellingBinPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), pkt);
    }
}
