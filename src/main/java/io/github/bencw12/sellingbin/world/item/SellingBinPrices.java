package io.github.bencw12.sellingbin.world.item;

import io.github.bencw12.sellingbin.SellingBin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SellingBinPrices extends ArrayList<SellingBinPrice> {

    private static final int NUM_PRICES = 15;
    public static final SellingBinPrices PRICES = new SellingBinPrices();

    public SellingBinPrices() {
        super();
    }
    public SellingBinPrices(int n) {
        super(n);
    }

    public static int getPrice(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        for (SellingBinPrice price : PRICES) {
            if (price.getItem().getItem() == stack.getItem()) {
                return price.getPrice() * stack.getCount();
            }
        }

        return 0;
    }

    public static void getNewPrices() {
        PRICES.clear();

        // get all edible foods
        ArrayList<Item> foods = new ArrayList<>();

        for (Item i : ForgeRegistries.ITEMS.getValues()) {
            if (i.isEdible()) {
                foods.add(i);
            }
        }

        ArrayList<Integer> idxs = new ArrayList<>();

        for (int i = 0; i < NUM_PRICES; ++i) {
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
            SellingBinPrice p = new SellingBinPrice(item, multiplier);
            PRICES.add(p);
        }

        SellingBin.PRICES_SAVE.setDirty();
    }

    public CompoundTag createTag(CompoundTag tag) {
        ListTag list = new ListTag();

        for (int i = 0; i < this.size(); i++) {
            SellingBinPrice price = this.get(i);
            list.add(price.createTag());
        }

        tag.put("SellingBinPrices", list);
        return tag;
    }
}
