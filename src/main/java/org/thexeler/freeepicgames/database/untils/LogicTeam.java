package org.thexeler.freeepicgames.database.untils;

public enum LogicTeam {
    ATTACKER,
    DEFENDER,
    NEUTRAL;

    public static LogicTeam fromString(String str) {
        return switch (str) {
            case "Attacker" -> LogicTeam.ATTACKER;
            case "Defender" -> LogicTeam.DEFENDER;
            default -> LogicTeam.NEUTRAL;
        };
    }

    public static String toString(LogicTeam team) {
        return switch (team) {
            case ATTACKER -> "Attacker";
            case DEFENDER -> "Defender";
            default -> "Neutral";
        };
    }
}