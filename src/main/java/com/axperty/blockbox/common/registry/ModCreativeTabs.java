package com.axperty.blockbox.common.registry;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import com.axperty.blockbox.BlockBox;
import java.util.function.Supplier;

public class ModCreativeTabs
{
	public static void register() {}

	private static Supplier<CreativeModeTab> register(String name, Supplier<CreativeModeTab> tabSupplier) {
		CreativeModeTab tab = tabSupplier.get();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(BlockBox.MOD_ID, name), tab);
		return () -> tab;
	}

	public static final Supplier<CreativeModeTab> TAB_BLOCK_BOX = register("example_tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
			.title(Component.translatable("itemGroup." + BlockBox.MOD_ID))
			.icon(() -> ModItems.CHISELED_GOLD.get().getDefaultInstance())
			.displayItems((parameters, output) -> ModItems.CREATIVE_TAB_ITEMS.forEach((item) -> output.accept(item.get())))
			.build());
}
