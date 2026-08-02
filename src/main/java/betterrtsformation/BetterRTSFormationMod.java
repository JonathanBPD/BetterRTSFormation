package betterrtsformation;

import arc.Events;
import mindustry.game.EventType;
import mindustry.mod.Mod;

public class BetterRTSFormationMod extends Mod {
    @Override
    public void init() {
        BetterRTSFormationFeature.init();

        Events.on(EventType.ClientLoadEvent.class, BetterRTSFormationFeature::loadClient);
    }
}
