/*
 *   OnTime - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.ontime.sponge.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.ontime.api.member.MemberManager;
import org.anvilpowered.ontime.api.registry.OnTimeKeys;
import org.anvilpowered.ontime.common.task.CommonSyncTaskService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Singleton
public class SpongeSyncTaskService extends CommonSyncTaskService {

    @Inject
    private MemberManager<Text> memberManager;

    @Inject
    private Environment environment;

    PermissionService permissionService;

    private Task task;

    @Inject
    public SpongeSyncTaskService(Registry registry) {
        super(registry);
        permissionService = Sponge.getServiceManager().provideUnchecked(PermissionService.class);
    }

    @Override
    public void startSyncTask() {
        Sponge.getServer().getConsole().sendMessage(Text.of(environment.getPluginInfo().getPrefix(), TextColors.AQUA, "Starting playtime sync task"));
        task = Task.builder().async().interval(1, TimeUnit.MINUTES).execute(getSyncTask()).submit(environment.getPlugin());
    }

    @Override
    public void stopSyncTask() {
        if (task != null) task.cancel();
    }

    @Override
    public Runnable getSyncTask() {
        return () -> {
            Collection<Subject> allGroups = permissionService.getGroupSubjects().getLoadedSubjects();
            Set<String> configRanks = registry.getOrDefault(OnTimeKeys.RANKS).keySet();
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                long time = 60;
                for (Map<String, String> options : player.getSubjectData().getAllOptions().values()) {
                    String multiplier = options.get(MULTIPLIER_META_KEY);
                    if (multiplier == null) {
                        continue;
                    }
                    try {
                        time *= Double.parseDouble(multiplier);
                    } catch (NumberFormatException e) {
                        logger.error("An error occurred parsing the time multiplier for " + player.getName(), e);
                    }
                }

                memberManager.sync(player.getUniqueId(), time).thenAcceptAsync(optionalRank -> {
                    if (!optionalRank.isPresent()) {
                        return;
                    }
                    String rank = optionalRank.get();
                    boolean hasNewRank = false;
                    for (Subject subject : allGroups) {
                        if (subject.getIdentifier().equals(rank)) {
                            player.getSubjectData().addParent(Collections.emptySet(), subject.asSubjectReference());
                            hasNewRank = true;
                        } else if (hasNewRank && player.getParents().size() == 1) {
                            break;
                        } else if (configRanks.contains(subject.getIdentifier())) {
                            player.getSubjectData().removeParent(Collections.emptySet(), subject.asSubjectReference());
                        }
                    }
                });
            }
        };
    }
}
