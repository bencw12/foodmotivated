package io.github.bencw12.sellingbin.network.packet;

import io.github.bencw12.sellingbin.world.item.SellingBinOffer;
import io.github.bencw12.sellingbin.world.item.SellingBinOffers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SellingBinOffersPacket {
    private final SellingBinOffers offers;

    public SellingBinOffersPacket(SellingBinOffers offers) {
        this.offers = offers;
    }

    public static void encode(SellingBinOffersPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.offers.size());
        synchronized (pkt.offers) {
            for (SellingBinOffer offer : pkt.offers) {
                offer.writeToBuf(buf);
            }
        }
    }

    public static void handle(SellingBinOffersPacket pkt, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            SellingBinOffers.OFFERS.clear();
            SellingBinOffers.OFFERS.addAll(pkt.offers);
        });
        context.get().setPacketHandled(true);
    }

    public static SellingBinOffersPacket decode(FriendlyByteBuf buf) {
        int num = buf.readInt();
        SellingBinOffers offers = new SellingBinOffers(num);
        for (int i = 0; i < num; i++) {
            ItemStack item = buf.readItem();
            double multiplier = buf.readDouble();
            SellingBinOffer o = new SellingBinOffer(item, multiplier);
            offers.add(o);
        }

        return new SellingBinOffersPacket(offers);
    }
}
