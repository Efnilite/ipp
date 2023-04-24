package dev.efnilite.ipp.mode;

import dev.efnilite.ip.api.Registry;
import dev.efnilite.ipp.mode.multi.DuelsMode;
import dev.efnilite.ipp.mode.multi.TeamSurvivalMode;
import dev.efnilite.ipp.mode.single.*;

public class PlusMode {

    // singleplayer
    public static HourglassMode HOURGLASS;
    public static LobbyMode LOBBY;
    public static PracticeMode PRACTICE;
    public static SpeedMode SPEED;
    public static SuperJumpMode SUPER_JUMP;
    public static TimeTrialMode TIME_TRIAL;
    public static WaveTrialMode WAVE_TRIAL;

    // multiplayer
    public static DuelsMode DUELS;
    public static TeamSurvivalMode TEAM_SURVIVAL;

    public static void init() {
        // singleplayer
        HOURGLASS = (HourglassMode) Registry.getMode("hourglass");
        LOBBY = (LobbyMode) Registry.getMode("lobby");
        PRACTICE = (PracticeMode) Registry.getMode("practice");
        WAVE_TRIAL = (WaveTrialMode) Registry.getMode("wave_trial");
        SPEED = (SpeedMode) Registry.getMode("speed");
        SUPER_JUMP = (SuperJumpMode) Registry.getMode("super_jump");
        TIME_TRIAL = (TimeTrialMode) Registry.getMode("time_trial");

        // multiplayer
        DUELS = (DuelsMode) Registry.getMode("duels");
        TEAM_SURVIVAL = (TeamSurvivalMode) Registry.getMode("team_survival");
    }
}