package com.hydowned.manager

import com.hydowned.config.ModConfig
import com.hydowned.hud.DownedHud
import com.hydowned.player.aspect.ModPlayer
import com.hydowned.logging.Log
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef

/**
 * Manages HUD instances for downed players.
 *
 * Creates one DownedHud instance per player and reuses it for updates.
 * Handles showing/hiding/updating the HUD based on player state.
 */
class HudManager(private val config: ModConfig) {

    private val huds: MutableMap<ModPlayer, DownedHud> = mutableMapOf()

    private fun addCustomHud(player: Player, playerRef: PlayerRef, customUIHud: DownedHud) {
        player.hudManager.addCustomHud(playerRef, customUIHud)
    }

    private fun removeCustomHud(player: Player, playerRef: PlayerRef) {
        player.hudManager.removeCustomHud(playerRef, "hydowned_downed")
    }

    /**
     * Shows or updates the HUD for a player.
     *
     * @param downed The downed player being viewed (whose state to display)
     * @param player The viewer (who sees the HUD)
     * @param visible Whether the HUD should be visible
     */
    fun showHud(
        downed: ModPlayer,
        player: ModPlayer,
        visible: Boolean
    ) {
        if (!huds.containsKey(player)) {
            val value = DownedHud(player.playerRef)
            huds[player] = value
            value.updateHud(downed, player)
            addCustomHud(player.player, player.playerRef, value)
            value.setVisible(visible)
        } else {
            val customUIHud = huds[player]!!
            customUIHud.setVisible(visible)
            customUIHud.updateHud(downed, player)
            customUIHud.show()
        }
    }

    /**
     * Old showHud signature - deprecated, kept for reference
     */
    /**
     * Hides and removes the HUD for a player.
     */
    fun hideHud(player: ModPlayer) {
        huds.remove(player)
        removeCustomHud(player.player, player.playerRef)
    }

    /**
     * Clears all HUD instances.
     * Called on plugin shutdown or reload.
     */
    fun clearAll() {
        Log.fine("HudManager", "Clearing all HUDs (${huds.size} instances)")
        for ((player, hud) in huds) {
            removeCustomHud(player.player, player.playerRef)
        }
        huds.clear()
    }
}