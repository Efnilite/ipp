package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ipp.gamemode.multi.DuelsGamemode;
import dev.efnilite.ipp.gamemode.multi.TeamSurvivalGamemode;
import dev.efnilite.ipp.gamemode.single.*;

public class PlusGamemodes {

    // singleplayer
    public static HourglassGamemode HOURGLASS;
    public static LobbyGamemode LOBBY;
    public static PracticeGamemode PRACTICE;
    public static SpeedGamemode SPEED;
    public static SpeedJumpGamemode SPEED_JUMP;
    public static TimeTrialGamemode TIME_TRIAL;

    // multiplayer
    public static DuelsGamemode DUELS;
    public static TeamSurvivalGamemode TEAM_SURVIVAL;

    public static void init() {
        // singleplayer
        HOURGLASS = (HourglassGamemode) IP.getRegistry().getGamemode("hourglass");
        LOBBY = (LobbyGamemode) IP.getRegistry().getGamemode("lobby");
        PRACTICE = (PracticeGamemode) IP.getRegistry().getGamemode("practice");
        SPEED = (SpeedGamemode) IP.getRegistry().getGamemode("speed");
        SPEED_JUMP = (SpeedJumpGamemode) IP.getRegistry().getGamemode("speed-jump");
        TIME_TRIAL = (TimeTrialGamemode) IP.getRegistry().getGamemode("time-trial");

        // multiplayer
        DUELS = (DuelsGamemode) IP.getRegistry().getGamemode("duels");
        TEAM_SURVIVAL = (TeamSurvivalGamemode) IP.getRegistry().getGamemode("teamsurvival");
    }
}