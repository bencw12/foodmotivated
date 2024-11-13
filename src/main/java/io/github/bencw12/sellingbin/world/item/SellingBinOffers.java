package io.github.bencw12.sellingbin.world.item;

import io.github.bencw12.sellingbin.SellingBin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SellingBinOffers extends ArrayList<SellingBinOffer> {

    private static final int NUM_OFFERS = 15;
    public static final SellingBinOffers OFFERS = new SellingBinOffers();

    public SellingBinOffers() {
        super();
    }
    public SellingBinOffers(int n) {
        super(n);
    }

    public static int getEmeraldTotal(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        for (SellingBinOffer offer : OFFERS) {
            if (offer.getItem().getItem() == stack.getItem()) {
                return offer.getOffer() * stack.getCount();
            }
        }

        return 0;
    }

    public static void getNewOffers() {
        OFFERS.clear();

        // get all edible foods
        ArrayList<Item> foods = new ArrayList<>();

        for (Item i : ForgeRegistries.ITEMS.getValues()) {
            if (i.isEdible()) {
                foods.add(i);
            }
        }

        ArrayList<Integer> idxs = new ArrayList<>();

        for (int i = 0; i < NUM_OFFERS; ++i) {
            int rnd = ThreadLocalRandom.current().nextInt(0, foods.size());
            if (!idxs.contains(rnd)) {
                idxs.add(rnd);
            } else {
                i--;
            }
        }

        for (int i : idxs) {
            ItemStack item = new ItemStack(foods.get(i));
            // TODO make dynamic
            double multiplier = 0.5;
            SellingBinOffer p = new SellingBinOffer(item, multiplier);
            OFFERS.add(p);
        }

        SellingBin.OFFERS_SAVE.setDirty();
    }

    public CompoundTag createTag(CompoundTag tag) {
        ListTag list = new ListTag();

        for (int i = 0; i < this.size(); i++) {
            SellingBinOffer offer = this.get(i);
            list.add(offer.createTag());
        }

        tag.put("SellingBinOffers", list);
        return tag;
    }
}
