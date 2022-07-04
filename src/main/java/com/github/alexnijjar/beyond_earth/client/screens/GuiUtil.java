package com.github.alexnijjar.beyond_earth.client.screens;

import java.awt.Rectangle;

import com.github.alexnijjar.beyond_earth.util.ModIdentifier;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiUtil {

    public static final int FIRE_WIDTH = 14;
    public static final int FIRE_HEIGHT = 14;
    public static final int ARROW_WIDTH = 24;
    public static final int ARROW_HEIGHT = 17;
    public static final int OXYGEN_TANK_WIDTH = 14;
    public static final int OXYGEN_TANK_HEIGHT = 48;
    public static final int ENERGY_WIDTH = 24;
    public static final int ENERGY_HEIGHT = 48;
    public static final int FUEL_WIDTH = 48;
    public static final int FUEL_HEIGHT = 48;
    public static final int FLUID_TANK_WIDTH = 14;
    public static final int FLUID_TANK_HEIGHT = 48;

    public static final Identifier FIRE_TEXTURE = new ModIdentifier("textures/fire_on.png");
    public static final Identifier ARROW_TEXTURE = new ModIdentifier("textures/animated_arrow_full.png");
    public static final Identifier OXYGEN_CONTENT_TEXTURE = new ModIdentifier("textures/oxygen.png");
    public static final Identifier ENERGY_TEXTURE = new ModIdentifier("textures/energy_full.png");
    public static final Identifier FLUID_TANK_TEXTURE = new ModIdentifier("textures/fluid_tank.png");

    public static boolean isHovering(Rectangle bounds, double x, double y) {
        double left = bounds.getX();
        double right = left + bounds.getWidth();
        double top = bounds.getY();
        double bottom = top + bounds.getHeight();
        return left <= x && x < right && top <= y && y < bottom;
    }

    public static Rectangle getFluidTankBounds(int x, int y) {
        return new Rectangle(x, y, FLUID_TANK_WIDTH, FLUID_TANK_HEIGHT);
    }

    public static Rectangle getArrowBounds(int x, int y) {
        return new Rectangle(x, y, ARROW_WIDTH, ARROW_HEIGHT);
    }

    public static Rectangle getEnergyBounds(int x, int y) {
        return new Rectangle(x, y, ENERGY_WIDTH, ENERGY_HEIGHT);
    }

    public static Rectangle getFireBounds(int x, int y) {
        return new Rectangle(x, y, FIRE_WIDTH, FIRE_HEIGHT);
    }

    public static void drawEnergy(MatrixStack matrixStack, int x, int y, long energy, long maxEnergy) {
        double ratio = maxEnergy > 0 ? createRatio(energy, maxEnergy) : 0;
        drawVertical(matrixStack, x, y, ENERGY_WIDTH, ENERGY_HEIGHT, ENERGY_TEXTURE, ratio);
    }

    public static void drawFluidTank(MatrixStack matrices, int x, int y, long fluidAmount, long fluidCapacity, FluidVariant fluid) {
        double ratio = fluidCapacity > 0 ? createRatio(fluidAmount, fluidCapacity) : 0;
        drawFluidTank(matrices, x, y, ratio, fluid);
    }

    public static void drawFluidTank(MatrixStack matrices, int x, int y, double ratio, FluidVariant fluid) {
        // Draw the fluid.
        drawFluid(matrices, x, y, ratio, fluid);
        // Draw the fluid tank.
        drawVertical(matrices, x, y, FLUID_TANK_WIDTH, FLUID_TANK_HEIGHT, FLUID_TANK_TEXTURE, 1.0);
    }

    public static void drawAccentingFluidTank(MatrixStack matrices, int x, int y, double ratio, FluidVariant fluid) {
        drawFluidTank(matrices, x, y, 1.0 - ratio, fluid);
    }

    private static void drawFluid(MatrixStack matrices, int x, int y, double ratio, FluidVariant fluid) {

        if (fluid.isBlank()) {
            return;
        }

        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        int colour = FluidVariantRendering.getColor(fluid);
        int spriteHeight = sprite.getHeight();

        RenderSystem.setShaderColor((colour >> 16 & 255) / 255.0f, (float) (colour >> 8 & 255) / 255.0f, (float) (colour & 255) / 255.0f, 1.0f);
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        MinecraftClient client = MinecraftClient.getInstance();
        double scale = (int) client.getWindow().getScaleFactor();
        int screenHeight = client.getWindow().getScaledHeight();

        int scissorX = (int)(x * scale);
        int scissorY = (int) (((screenHeight - y - FLUID_TANK_HEIGHT)) * scale);
        int scissorWidth = (int)(FLUID_TANK_WIDTH * scale);
        int scissorHeight = (int) ((FLUID_TANK_HEIGHT - 1) * scale * ratio);

        // First, the sprite is rendered in full, and then it is masked based on how full the fluid tank is.
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        for (int i = 1; i < 4; i++) {
            DrawableHelper.drawSprite(matrices, x + 1, FLUID_TANK_HEIGHT + y - (spriteHeight * i) - 1, 0, FLUID_TANK_WIDTH - 2, spriteHeight, sprite);
        }

        RenderSystem.disableScissor();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawFire(MatrixStack matrixStack, int x, int y, short burnTime, short totalBurnTime) {
        double ratio = totalBurnTime > 0 ? createRatio(burnTime, totalBurnTime) : 0;
        drawVertical(matrixStack, x, y, FIRE_WIDTH, FIRE_HEIGHT, FIRE_TEXTURE, ratio);
    }

    public static void drawArrow(MatrixStack matrixStack, int x, int y, short burnTime, short totalBurnTime) {
        double ratio = totalBurnTime > 0 ? createRatio(burnTime, totalBurnTime) : 0;
        drawHorizontal(matrixStack, x, y, ARROW_WIDTH, ARROW_HEIGHT, ARROW_TEXTURE, ratio);
    }

    public static void drawVertical(MatrixStack matrixStack, int x, int y, int width, int height, Identifier resource, double ratio) {
        int ratioHeight = (int) Math.ceil(height * ratio);
        int remainHeight = height - ratioHeight;
        RenderSystem.setShaderTexture(0, resource);
        DrawableHelper.drawTexture(matrixStack, x, y + remainHeight, 0, remainHeight, width, ratioHeight, width, height);
    }

    public static void drawAccentingVertical(MatrixStack matrixStack, int x, int y, int width, int height, Identifier resource, double ratio) {
        ratio = 1.0 - ratio;
        drawVertical(matrixStack, x, y, width, height, resource, ratio);
    }

    public static void drawVerticalReverse(MatrixStack matrixStack, int x, int y, int width, int height, Identifier resource, double ratio) {
        int ratioHeight = (int) Math.ceil(height * ratio);
        int remainHeight = height - ratioHeight;
        RenderSystem.setShaderTexture(0, resource);
        DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, width, remainHeight, width, height);
    }

    public static void drawHorizontal(MatrixStack matrixStack, int x, int y, int width, int height, Identifier resource, double ratio) {
        int ratioWidth = (int) Math.ceil(width * ratio);

        RenderSystem.setShaderTexture(0, resource);
        DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, ratioWidth, height, width, height);
    }

    public static double createRatio(long a, long b) {
        return (double) a / (double) b;
    }

    public static double createRatio(short a, short b) {
        return (float) a / (float) b;
    }
}