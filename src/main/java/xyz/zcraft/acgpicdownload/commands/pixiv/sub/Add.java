package xyz.zcraft.acgpicdownload.commands.pixiv.sub;

import xyz.zcraft.acgpicdownload.commands.pixiv.Profile;
import xyz.zcraft.acgpicdownload.commands.pixiv.SubCommand;
import xyz.zcraft.acgpicdownload.util.pixiv.PixivArtwork;

import java.util.LinkedList;
import java.util.List;

public class Add extends SubCommand {
    public List<PixivArtwork> invoke(List<String> argList, Profile profile, List<PixivArtwork> previous) throws Exception {
        List<String> idStrings = new LinkedList<>();

        for (int i = 1; i < argList.size(); i++) {
            String e = argList.get(i);
            idStrings.addAll(List.of(e.split(",")));
        }

        List<PixivArtwork> result = new LinkedList<>();

        for (String idString : idStrings) {
            if (idString.trim().isEmpty()) continue;
            try {
                int id = Integer.parseInt(idString.trim());
                PixivArtwork newArtwork = new PixivArtwork();
                newArtwork.setId(String.valueOf(id));
                result.add(newArtwork);
            } catch (NumberFormatException e) {
                out.err("Invalid artwork ID: " + idString + ", skipping.");
            }
        }

        out.info("Added " + result.size() + " artworks.");
        if (previous != null && !previous.isEmpty()) {
            out.info("Appending to previous " + previous.size() + " artwork data, now total: " + (previous.size() + result.size()));
            previous.addAll(result);
            return previous;
        } else {
            return result;
        }
    }
}
