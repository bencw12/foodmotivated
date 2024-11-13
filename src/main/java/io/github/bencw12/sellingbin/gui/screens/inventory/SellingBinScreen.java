package io.github.bencw12.sellingbin.gui.screens.inventory;

import io.github.bencw12.sellingbin.SellingBin;
import io.github.bencw12.sellingbin.gui.inventory.SellingBinMenu;
import io.github.bencw12.sellingbin.item.SellingBinItems;
import io.github.bencw12.sellingbin.world.item.SellingBinPrice;
import io.github.bencw12.sellingbin.world.item.SellingBinPrices;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class SellingBinScreen extends AbstractContainerScreen<SellingBinMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SellingBin.MOD_ID, "textures/gui/selling_bin.png");
    private static final int TEXT_COLOR = 0x404040;
    private static final int PRICES_X = 6;
    private static final int PRICES_Y = 6;
    private static final int TITLE_X = 74;
    private static final int TITLE_Y = 6;
    private static final int INVENTORY_X = 74;
    private static final int INVENTORY_Y = 73;
    private static final int BG_WIDTH = 242;
    private static final int BG_HEIGHT = 166;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int SCROLL_Y = 168;
    private static final int SCROLL_WIDTH = 6;
    private static final int MAX_PRICE_BOXES = 7;
    private static final int SCROLL_BAR_HEIGHT = 140;
    private static final int SCROLL_BAR_X = 60;
    private static final int SCROLL_BAR_Y = 18;
    private static final int SCROLL_BUTTON_HEIGHT = 27;

    int scrollOff;
    private boolean isDragging;

    public SellingBinScreen(SellingBinMenu container, Inventory inv, Component component) {
        super(container, inv, component);
        this.imageWidth = BG_WIDTH;
        this.imageHeight = BG_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float alpha, int x1, int y1) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, 0.0F, this.imageWidth, this.imageHeight, BG_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        // prices window
        gui.drawString(this.font,
                Component.translatable("block.selling_bin.prices"),
                PRICES_X,
                PRICES_Y,
                TEXT_COLOR,
                false);
        // title
        gui.drawString(this.font,
                Component.translatable("block.selling_bin.selling_bin"),
                TITLE_X,
                TITLE_Y,
                TEXT_COLOR,
                false);

        // inventory label
        gui.drawString(this.font,
                Component.translatable("container.inventory"),
                INVENTORY_X,
                INVENTORY_Y,
                TEXT_COLOR,
                false);
    }

    private int getNumPrices() {
        return SellingBinPrices.PRICES.size();
    }

    private void renderScroller(GuiGraphics gui, int x, int y) {
        int i = getNumPrices() - MAX_PRICE_BOXES;
        if (i > 1) {
            int j = (SCROLL_BAR_HEIGHT - 1) - (SCROLL_BUTTON_HEIGHT + (i - 1) * (SCROLL_BAR_HEIGHT - 1) / i);
            int k = 1 + j / i + (SCROLL_BAR_HEIGHT - 1) / i;
            int offset = Math.min(SCROLL_BAR_HEIGHT - SCROLL_BUTTON_HEIGHT, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                offset = SCROLL_BAR_HEIGHT - SCROLL_BUTTON_HEIGHT;
            }

            gui.blit(TEXTURE, x + SCROLL_BAR_X, y + SCROLL_BAR_Y + offset, 0, 0.0F, (float) SCROLL_Y, SCROLL_WIDTH, SCROLL_BUTTON_HEIGHT, BG_WIDTH, TEXTURE_HEIGHT);
        } else {
            gui.blit(TEXTURE, x + SCROLL_BAR_X, y + SCROLL_BAR_Y, 0, (float) SCROLL_WIDTH, (float) SCROLL_Y, SCROLL_WIDTH, SCROLL_BUTTON_HEIGHT, BG_WIDTH, TEXTURE_HEIGHT);
        }
    }


    @Override
    public void render(@NotNull GuiGraphics gui, int x, int y, float a) {
        this.renderBackground(gui);
        super.render(gui, x, y, a);

        int guiX = (this.width - this.imageWidth) / 2;
        int guiY = (this.height - this.imageHeight) / 2;
        int offerY = guiY + SCROLL_BAR_Y - 1;
        this.renderScroller(gui, guiX, guiY);
        int offset = 0;


        ArrayList<OfferListing> offers = new ArrayList<>();

        for (SellingBinPrice p : SellingBinPrices.PRICES) {
            if (!this.canScroll() || offset >= this.scrollOff && offset < MAX_PRICE_BOXES + this.scrollOff) {
                OfferListing offer = new OfferListing(guiX, offerY, offset, this.font, p);
                offers.add(offer);
                offer.render(gui, x, y, a);
                offerY += 20;
            }
            offset++;
        }

        for (OfferListing offer : offers) {
            offer.renderToolTip(gui, x, y);
        }

        this.renderTooltip(gui, x, y);
    }

    @OnlyIn(Dist.CLIENT)
    static class OfferListing extends AbstractWidget {
        final int index;
        final SellingBinPrice price;
        private final int FOOD_X_OFF = 6;
        private final int EMERALD_X_OFF = 39;
        private final int ITEM_Y_OFF = 2;
        private final int ARROW_X_OFF = 28;
        private final int ARROW_Y_OFF = 5;
        private final int ARROW_TEXTURE_X = 13;
        private final int ARROW_TEXTURE_Y = 168;
        private final int ARROW_TEXTURE_WIDTH = 10;
        private final int ARROW_TEXTURE_HEIGHT = 9;
        static int OFFER_WIDTH = 54;
        static int OFFER_HEIGHT = 20;

        private final Font font;

        public OfferListing(int x, int y, int index, Font font, SellingBinPrice price) {
            super(x, y, OFFER_WIDTH, OFFER_HEIGHT, CommonComponents.EMPTY);
            this.index = index;
            this.price = price;
            this.font = font;
        }

        public int getIndex() {
            return index;
        }

        @Override
        protected void renderWidget(GuiGraphics gui, int x, int y, float a) {

            RenderSystem.enableBlend();
            gui.blit(TEXTURE, this.getX() + ARROW_X_OFF, this.getY() + ARROW_Y_OFF,
                    0, (float)ARROW_TEXTURE_X, (float)ARROW_TEXTURE_Y,
                    ARROW_TEXTURE_WIDTH, ARROW_TEXTURE_HEIGHT, BG_WIDTH, TEXTURE_HEIGHT);

            ItemStack foodItem = this.price.getItem();
            int price = this.price.getPrice();
            ItemStack emeralds = new ItemStack(SellingBinItems.EMERALD_NUGGET_ITEM.get(), price);
            emeralds.setCount(price);

            gui.pose().pushPose();
            gui.pose().translate(0.0F, 0.0F, 100.0F);

            gui.renderItem(foodItem, this.getX() + FOOD_X_OFF, this.getY() + ITEM_Y_OFF);
            gui.renderItemDecorations(this.font, foodItem, this.getX() + FOOD_X_OFF, this.getY() + ITEM_Y_OFF);
            gui.renderFakeItem(emeralds, this.getX() + EMERALD_X_OFF, this.getY() + ITEM_Y_OFF);
            gui.renderItemDecorations(this.font, emeralds, this.getX() + EMERALD_X_OFF, this.getY() + ITEM_Y_OFF);
            gui.pose().popPose();

        }

        public void renderToolTip(GuiGraphics gui, int x, int y) {
            if (this.isHovered) {
                if (x < this.getX() + 20) {
                    ItemStack item = this.price.getItem();
                    gui.renderTooltip(this.font, item, x, y);
                }

                if (x > this.getX() + EMERALD_X_OFF && x < this.getX() + EMERALD_X_OFF + 20) {
                    int price = this.price.getPrice();
                    gui.renderTooltip(this.font, new ItemStack(SellingBinItems.EMERALD_NUGGET_ITEM.get(), price), x, y);
                }
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput out) {
        }
    }

    private boolean canScroll() {
        return getNumPrices() > MAX_PRICE_BOXES;
    }

    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_) {
        int i = getNumPrices();
        if (this.canScroll()) {
            int j = i - MAX_PRICE_BOXES;
            this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - p_94688_), 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double p_97752_, double p_97753_, int p_97754_, double p_97755_, double p_97756_) {
        int i = getNumPrices();
        if (this.isDragging) {
            int j = this.topPos + SCROLL_BAR_Y;
            int k = j + SCROLL_BAR_HEIGHT - 1;
            int l = i - MAX_PRICE_BOXES;
            float f = ((float)p_97753_ - (float)j - ((float)SCROLL_BUTTON_HEIGHT) / 2.0F) / ((float)(k - j) - (float)SCROLL_BUTTON_HEIGHT);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int id) {
        this.isDragging = false;
        int topX = (this.width - this.imageWidth) / 2;
        int topY = (this.height - this.imageHeight) / 2;
        if (this.canScroll() &&
                x > (double)(topX + SCROLL_BAR_X) &&
                x < (double)(topX + SCROLL_BAR_X + SCROLL_WIDTH) &&
                y > (double)(topY + SCROLL_BAR_Y) &&
                y <= (double)(topY + SCROLL_BAR_Y + SCROLL_BAR_HEIGHT)) {
            this.isDragging = true;
        }

        return super.mouseClicked(x, y, id);
    }
}
