import java.io.File;
import java.io.IOException;

import Util.DownloadUtil;
import Util.SourceUtil.SourceFetcher;
import Util.SourceUtil.SourceManager;

class Test{
    public static void main(String[] args) throws IOException {
        DownloadUtil.download(SourceFetcher.fetch(SourceManager.parse(SourceManager.readConfig()).get(0))[0], new File(""));
    }
}