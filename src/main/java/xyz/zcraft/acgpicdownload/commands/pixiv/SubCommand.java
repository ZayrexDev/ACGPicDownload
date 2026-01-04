package xyz.zcraft.acgpicdownload.commands.pixiv;

import xyz.zcraft.acgpicdownload.util.Logger;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.util.List;

public abstract class SubCommand {
    protected final org.apache.log4j.Logger log;
    protected final Logger out;

    public SubCommand() {
        log = org.apache.log4j.Logger.getLogger(this.getClass());
        out = new Logger(this.getClass().getSimpleName());
    }

    public abstract List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) throws Exception;
}
