package io.github.bencw12.sellingbin.gui.inventory;

import io.github.bencw12.sellingbin.gui.SellingBinGui;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SellingBinMenu extends AbstractContainerMenu {

    public static int ROWS = 3;
    public static int COLS = 9;
    public static int SLOT_SIDE = 18;
    public static int BIN_INV_Y_START = 18;
    public static int BIN_INV_X_START = 74;
    public static int PLAYER_INV_Y_START = 102;
    public static int PLAYER_INV_X_START = 74;
    public static int PLAYER_HOTBAR_Y_START = 160;

    private Container container;

    public SellingBinMenu(int id, Inventory playerInventory, ItemStackHandler handler) {
        super(SellingBinGui.SELLING_BIN_MENU.get(), id);

        int i = (ROWS - 4) * SLOT_SIDE;

        for(int j = 0; j < ROWS; ++j) {
            for(int k = 0; k < COLS; ++k) {
                this.addSlot(new SlotItemHandler(handler,
                        k + j * 9,
                        BIN_INV_X_START + k * SLOT_SIDE,
                        BIN_INV_Y_START + j * SLOT_SIDE));
            }
        }

        for(int j = 0; j < 3; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory,
                        k + j * 9 + 9,
                        PLAYER_INV_X_START + k * SLOT_SIDE,
                        PLAYER_INV_Y_START + j * SLOT_SIDE + i));
            }
        }

        for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory,
                    j,
                    PLAYER_INV_X_START + j * SLOT_SIDE,
                    PLAYER_HOTBAR_Y_START + i));
        }
    }

    public SellingBinMenu(int i, Inventory inventory) {
        this(i, inventory, new ItemStackHandler(ROWS * COLS));
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIdx) {
        // Logic for moving items between inventory and chest
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIdx);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIdx < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public Container getContainer() {
        return container;
    }
}
