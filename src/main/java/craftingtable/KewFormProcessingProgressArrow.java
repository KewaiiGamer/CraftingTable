package craftingtable;

import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormCustomDraw;

import java.awt.*;

public class KewFormProcessingProgressArrow extends FormCustomDraw {
    protected CraftingTableProcessingHelp help;

    public KewFormProcessingProgressArrow(int x, int y, CraftingTableProcessingHelp help) {
        super(x, y, 32, 32);
        this.help = help;
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int leftPadding = 2;
        int rightPadding = 2;
        CraftingTableProcessedTechInventoryObjectEntity.TechProcessingHelp techProcessingHelp = (CraftingTableProcessedTechInventoryObjectEntity.TechProcessingHelp) help;
        Settings.UI.processing_arrow_empty.initDraw().draw(this.getX(), this.getY());
        if (techProcessingHelp.currentRecipeCanCraft != null && techProcessingHelp.currentRecipeCanCraft.canCraft()) {
            int width = Settings.UI.processing_arrow_full.getWidth() - (leftPadding + rightPadding);
            Settings.UI.processing_arrow_full.initDraw().section(leftPadding, leftPadding + (int) ((float) width), 0, Settings.UI.processing_arrow_full.getHeight()).draw(this.getX() + leftPadding, this.getY());
        }
    }

}
