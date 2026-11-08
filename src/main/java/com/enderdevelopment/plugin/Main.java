package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class Main extends JavaPlugin implements Listener, CommandExecutor {
    private static final long BOOST_COOLDOWN_MILLIS = 1500L;
    private static final double BOOST_FORWARD = 1.15D;
    private static final double BOOST_UPWARD = 0.85D;

    private NamespacedKey jetpackKey;
    private final Map<UUID, Long> boostCooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        jetpackKey = new NamespacedKey(this, "jetpack");
        registerJetpackRecipe();
        Bukkit.getPluginManager().registerEvents(this, this);
        if (getCommand("jetpack") != null) {
            getCommand("jetpack").setExecutor(this);
        }
    }

    @Override
    public void onDisable() {
        boostCooldowns.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        ItemStack jetpack = createJetpack();
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(jetpack);
        leftovers.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        player.sendMessage(Component.text("Jetpack added to your inventory.", NamedTextColor.GREEN));
        return true;
    }

    @EventHandler
    public void onSneakRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !isRightClick(event.getAction())) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isSneaking() || !isJetpack(player.getInventory().getChestplate())) {
            return;
        }

        long now = System.currentTimeMillis();
        long lastBoost = boostCooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now - lastBoost < BOOST_COOLDOWN_MILLIS) {
            return;
        }

        boostCooldowns.put(player.getUniqueId(), now);
        Vector direction = player.getLocation().getDirection().normalize();
        Vector boost = direction.multiply(BOOST_FORWARD).setY(BOOST_UPWARD);
        player.setVelocity(player.getVelocity().multiply(0.35D).add(boost));
        player.setGliding(true);
        event.setCancelled(true);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (player.isOnline() && isJetpack(player.getInventory().getChestplate()) && player.isOnGround()) {
                player.setGliding(false);
            }
        }, 10L);
    }

    private void registerJetpackRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(this, "jetpack_recipe");
        Bukkit.removeRecipe(recipeKey);

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, createJetpack());
        recipe.shape("IFI", "CEC", "IFI");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('F', Material.FIREWORK_ROCKET);
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('E', Material.ELYTRA);
        Bukkit.addRecipe(recipe);
    }

    private ItemStack createJetpack() {
        ItemStack item = new ItemStack(Material.ELYTRA);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.displayName(Component.text("Jetpack", NamedTextColor.AQUA));
        meta.lore(java.util.List.of(
                Component.text("Wear it as a chestplate.", NamedTextColor.GRAY),
                Component.text("Sneak and right-click to boost.", NamedTextColor.GRAY),
                Component.text("Protection IV", NamedTextColor.BLUE)
        ));
        meta.getPersistentDataContainer().set(jetpackKey, PersistentDataType.BYTE, (byte) 1);
        Enchantment protection = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("protection"));
        if (protection != null) {
            meta.addEnchant(protection, 4, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private boolean isJetpack(ItemStack item) {
        if (item == null || item.getType() != Material.ELYTRA) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(jetpackKey, PersistentDataType.BYTE);
    }

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
