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

package rocks.milspecsg.msontime.api.data.key;

import rocks.milspecsg.msrepository.api.data.key.Key;
import rocks.milspecsg.msrepository.api.data.key.Keys;

import java.util.HashMap;
import java.util.Map;

public final class MSOnTimeKeys {

    public static final Key<Map<String, Integer>> RANKS = new Key<Map<String, Integer>>("RANKS", new HashMap<>()) {
    };
    public static final Key<String> CHECK_PERMISSION = new Key<String>("CHECK_PERMISSION", "msontime.user.check") {
    };
    public static final Key<String> CHECK_EXTENDED_PERMISSION = new Key<String>("CHECK_EXTENDED_PERMISSION", "msontime.admin.check") {
    };
    public static final Key<String> EDIT_PERMISSION = new Key<String>("EDIT_PERMISSION", "msontime.admin.edit") {
    };
    public static final Key<String> IMPORT_PERMISSION = new Key<String>("IMPORT_PERMISSION", "msontime.admin.import") {
    };

    static {
        Keys.registerKey(RANKS);
        Keys.registerKey(CHECK_PERMISSION);
        Keys.registerKey(CHECK_EXTENDED_PERMISSION);
        Keys.registerKey(EDIT_PERMISSION);
        Keys.registerKey(IMPORT_PERMISSION);
    }

    private MSOnTimeKeys() {
        throw new AssertionError("**boss music** No instance for you!");
    }
}
