package com.hijackster99.ancientrelics.core;

import org.slf4j.Logger;

import com.hijackster99.ancientrelics.blocks.OreBlock;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AncientRelics.MODID)
public class AncientRelics
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "ancientrelics";
    
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Create a Deferred Register to hold Blocks
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    
    // Create a Deferred Register to hold Items
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    
    // Create a Deferred Register to hold CreativeModeTabs
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    // Blocks
    public static final DeferredBlock<OreBlock> PERIDOT_ORE = BLOCKS.register("peridot_ore", OreBlock::new);
    public static final DeferredBlock<OreBlock> RUBY_ORE = BLOCKS.register("ruby_ore", OreBlock::new);
    public static final DeferredBlock<OreBlock> SAPPHIRE_ORE = BLOCKS.register("sapphire_ore", OreBlock::new);
    
    // BlockItems
    public static final DeferredItem<BlockItem> PERIDOT_ORE_BLOCKITEM = ITEMS.registerSimpleBlockItem("peridot_ore", PERIDOT_ORE);
    public static final DeferredItem<BlockItem> RUBY_ORE_BLOCKITEM = ITEMS.registerSimpleBlockItem("ruby_ore", RUBY_ORE);
    public static final DeferredItem<BlockItem> SAPPHIRE_ORE_BLOCKITEM = ITEMS.registerSimpleBlockItem("sapphire_ore", SAPPHIRE_ORE);

    // Items
    public static final DeferredItem<Item> RAW_PERIDOT = ITEMS.registerSimpleItem("raw_peridot");    
    public static final DeferredItem<Item> RAW_RUBY = ITEMS.registerSimpleItem("raw_ruby");    
    public static final DeferredItem<Item> RAW_SAPPHIRE = ITEMS.registerSimpleItem("raw_sapphire");

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AR_TAB = CREATIVE_MODE_TABS.register("ar_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ancientrelics")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RAW_PERIDOT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
            	// Add the example item to the tab. For your own tabs, this method is preferred over the event
            	//output.acceptAll(Sets.newHashSet(EXAMPLE_ITEM.toStack(), PERIDOT_ORE_BLOCKITEM.toStack(), RUBY_ORE_BLOCKITEM.toStack(), SAPPHIRE_ORE_BLOCKITEM.toStack(), RAW_PERIDOT.toStack(), RAW_RUBY.toStack(), RAW_SAPPHIRE.toStack()));
            	output.accept(PERIDOT_ORE_BLOCKITEM.get());
            	output.accept(RUBY_ORE_BLOCKITEM.get());
            	output.accept(SAPPHIRE_ORE_BLOCKITEM.get());
            	output.accept(RAW_PERIDOT.get());
            	output.accept(RAW_RUBY.get());
            	output.accept(RAW_SAPPHIRE.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public AncientRelics(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered, repeat for items and tabs
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
