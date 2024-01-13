/*
 *   OnTime - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.ontime.velocity

import com.google.inject.Inject
import com.google.inject.Injector
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.velocity.command.toVelocity
import org.anvilpowered.anvil.velocity.createVelocity
import org.anvilpowered.ontime.api.OnTimeApi
import org.anvilpowered.ontime.core.OnTimePlugin
import org.koin.dsl.koinApplication

@Plugin(
    id = "ontime",
    name = "OnTime",
    version = "0.4.0-SNAPSHOT",
    authors = ["AnvilPowered"],
    dependencies = [Dependency(id = "luckperms")],
)
class OnTimeVelocityBootstrap @Inject constructor(
    private val injector: Injector,
    private val proxyServer: ProxyServer,
) {

    private lateinit var plugin: OnTimePlugin

    @Subscribe
    @Suppress("UNUSED_PARAMETER")
    fun onInit(event: ProxyInitializeEvent) {
        val anvil = AnvilApi.createVelocity(injector)
        plugin = koinApplication { modules(OnTimeApi.createVelocity(anvil).module) }.koin.get()
        plugin.enable()
        plugin.registerCommands { proxyServer.commandManager.register(BrigadierCommand(it.toVelocity())) }
    }

    @Subscribe
    @Suppress("UNUSED_PARAMETER")
    fun onDisable(event: ProxyShutdownEvent) {
        plugin.disable()
    }
}
