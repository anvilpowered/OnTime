/*
 *     MSOnTime - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msontime.common.member.repository;

import rocks.milspecsg.msontime.api.member.repository.MemberRepository;
import rocks.milspecsg.msontime.api.model.member.Member;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.common.repository.CommonRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CommonMemberRepository<
    TKey,
    TDataStore>
    extends CommonRepository<TKey, Member<TKey>, TDataStore>
    implements MemberRepository<TKey, TDataStore> {

    protected CommonMemberRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Member<TKey>> getTClass() {
        return (Class<Member<TKey>>) getDataStoreContext().getEntityClassUnsafe("member");
    }

    @Override
    public CompletableFuture<Optional<Member<TKey>>> getOneOrGenerateForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Member<TKey>> optionalMember = getOneForUser(userUUID).join();
                if (optionalMember.isPresent()) return optionalMember;
                // if there isn't one already, create a new one
                Member<TKey> member = generateEmpty();
                member.setBonusTime(0);
                member.setPlayTime(0);
                member.setUserUUID(userUUID);
                return insertOne(member).join();
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Member<TKey>>> generateUserFromConfig(UUID userUUID, int time) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Member<TKey>> optionalMember = getOneForUser(userUUID).join();
                if (optionalMember.isPresent()) return optionalMember;
                //If the user doens't exist in the db, create it from the values
                //Specified in the config
                Member<TKey> member = generateEmpty();
                member.setBonusTime(0);
                member.setPlayTime(time);
                member.setUserUUID(userUUID);
                return insertOne(member).join();
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }
}
