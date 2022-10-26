package earth.terrarium.ad_astra.forge;

import earth.terrarium.ad_astra.AdAstra;
import earth.terrarium.ad_astra.client.AdAstraClient;
import earth.terrarium.ad_astra.client.forge.AdAstraClientForge;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AdAstra.MOD_ID)
public class AdAstraForge {
    public AdAstraForge() {
        EventBuses.registerModEventBus(AdAstra.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AdAstra.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(AdAstraForge::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AdAstraForge::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> AdAstraClientForge::init);
        MinecraftForge.EVENT_BUS.addListener(AdAstraForge::onServerReloadListeners);
    }

    public static void onServerReloadListeners(AddReloadListenerEvent event) {
        AdAstra.onRegisterReloadListeners((id, listener) -> event.addListener(listener));
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        AdAstra.postInit();
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        AdAstraClient.initializeClient();
        AdAstraClientForge.postInit();
    }
}