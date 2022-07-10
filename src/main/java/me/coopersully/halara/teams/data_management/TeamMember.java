package me.coopersully.halara.teams.data_management;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.UUID;

public class TeamMember {

    private final UUID uuid;
    private final int position;
    private final long birthdate;

    public TeamMember(UUID uuid, int position, long birthdate) {
        this.uuid = uuid;
        this.position = position;
        this.birthdate = birthdate;
    }

    public TeamMember(@NotNull Player player, int position) {
        this.uuid = player.getUniqueId();
        this.position = position;
        this.birthdate = System.currentTimeMillis();
    }

    public TeamMember(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.position = 0;
        this.birthdate = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPosition() {
        return position;
    }

    public long getBirthdate() {
        return birthdate;
    }

    public JSONObject getJSONObject() {
        return new JSONObject("{ \"uuid\": \"" + uuid + "\", \"position\": " + position + ", \"birthdate\": \"" + birthdate + "\" }");
    }

    public static @NotNull TeamMember getFromJSONObject(@NotNull JSONObject jsonObject) {
        return new TeamMember(
                UUID.fromString(jsonObject.getString("uuid")),
                jsonObject.getInt("position"),
                jsonObject.getLong("birthdate")
        );
    }

    @Override
    public String toString() {
        return getJSONObject().toString();
    }

}
