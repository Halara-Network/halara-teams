package me.coopersully.halara.teams.data_management;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss z");
    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("MM/dd/yyyy");

    private final int id;
    private final String name;
    private final long birthdate;
    private final List<TeamMember> members = new ArrayList<>();

    public Team(@NotNull String displayname) {
        this.name = displayname.strip();
        this.id = getIDFromName(name);
        this.birthdate = System.currentTimeMillis();
    }

    public Team(int id, @NotNull String displayname, @NotNull long birthdate) {
        this.id = id;
        this.name = displayname;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public long getBirthdate() {
        return birthdate;
    }

    public String getBirthdatePretty() {
        return formatDate(birthdate);
    }

    public void addMember(Player player, int position) {
        this.members.add(new TeamMember(player, position));
    }

    public void addMember(TeamMember member) {
        this.members.add(member);
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public JSONArray getMembersJson() {
        JSONArray membersArray = new JSONArray();
        for (var member : members) {
            membersArray.put(member.getJSONObject());
        }
        return membersArray;
    }

    @Override
    public String toString() {
        return "[ " + id + ", " + name + ", " + birthdate + ", " + members.size() + " members"  + " ]";
    }

    public static int getIDFromName(@NotNull String name) {
        name = name.toLowerCase().strip();
        int hash = 7;
        for (int i = 0; i < name.length(); i++) {
            hash = hash * 31 + name.charAt(i);
        }
        return hash;
    }

    public static @NotNull String formatDate(long date) {
        return dateFormat.format(date);
    }

    public static @NotNull String formatDateShort(long date) {
        return dateFormatShort.format(date);
    }
}
