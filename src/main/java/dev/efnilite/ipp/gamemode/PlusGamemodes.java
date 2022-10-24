package dev.efnilite.ipp.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ipp.gamemode.multi.DuelsGamemode;
import dev.efnilite.ipp.gamemode.multi.TeamSurvivalGamemode;
import dev.efnilite.ipp.gamemode.single.*;

public class PlusGamemodes {

    // singleplayer
    public static HourglassGamemode HOURGLASS;
    public static LobbyGamemode LOBBY;
    public static FallTrialGamemode FALL_TRIAL;
    public static PracticeGamemode PRACTICE;
    public static RiseTrialGamemode RISE_TRIAL;
    public static SpeedGamemode SPEED;
    public static SuperJumpGamemode SUPER_JUMP;
    public static TimeTrialGamemode TIME_TRIAL;

    // multiplayer
    public static DuelsGamemode DUELS;
    public static TeamSurvivalGamemode TEAM_SURVIVAL;

    public static void init() {
        // singleplayer
        HOURGLASS = (HourglassGamemode) IP.getRegistry().getGamemode("hourglass");
        LOBBY = (LobbyGamemode) IP.getRegistry().getGamemode("lobby");
        FALL_TRIAL = (FallTrialGamemode) IP.getRegistry().getGamemode("fall_trial");
        PRACTICE = (PracticeGamemode) IP.getRegistry().getGamemode("practice");
        RISE_TRIAL = (RiseTrialGamemode) IP.getRegistry().getGamemode("rise_trial");
        SPEED = (SpeedGamemode) IP.getRegistry().getGamemode("speed");
        SUPER_JUMP = (SuperJumpGamemode) IP.getRegistry().getGamemode("super_jump");
        TIME_TRIAL = (TimeTrialGamemode) IP.getRegistry().getGamemode("time_trial");

        // multiplayer
        DUELS = (DuelsGamemode) IP.getRegistry().getGamemode("duels");
        TEAM_SURVIVAL = (TeamSurvivalGamemode) IP.getRegistry().getGamemode("team_survival");
    }
}