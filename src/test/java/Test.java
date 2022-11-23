import java.io.IOException;

import com.alibaba.fastjson2.JSONException;

import xyz.zcraft.acgpicdownload.Main;
import xyz.zcraft.acgpicdownload.util.sourceutil.Source;
import xyz.zcraft.acgpicdownload.util.sourceutil.SourceManager;

public class Test {
    public static void main(String[] args) throws JSONException, IOException {
        SourceManager.readConfig();
        Source source = SourceManager.getSources().get(0);
        Main.main("-m --arg num=1".split(" "));
    }
}
