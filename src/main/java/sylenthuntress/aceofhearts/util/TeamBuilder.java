package sylenthuntress.aceofhearts.util;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamBuilder {
    private final String name;
    private Formatting color;
    private final List<ScoreHolder> members = new ArrayList<>();

    public static TeamBuilder create(String name) {
        return new TeamBuilder(name);
    }

    TeamBuilder(String name) {
        this.name = name;
    }

    public TeamBuilder setColor(Formatting color) {
        this.color = color;
        return this;
    }

    public TeamBuilder addMembers(ScoreHolder... members) {
        this.members.addAll(Arrays.asList(members));
        return this;
    }

    public Team build(Scoreboard scoreboard) {
        Team team = scoreboard.getTeam(name);
        if (team != null) {
            for (String scoreHolder : members.stream().map(ScoreHolder::getNameForScoreboard).toList()) {
                scoreboard.addScoreHolderToTeam(scoreHolder, team);
            }

            return team;
        }

        team = scoreboard.addTeam(name);
        team.setColor(color);

        for (String scoreHolder : members.stream().map(ScoreHolder::getNameForScoreboard).toList()) {
            scoreboard.addScoreHolderToTeam(scoreHolder, team);
        }

        return team;
    }
}
