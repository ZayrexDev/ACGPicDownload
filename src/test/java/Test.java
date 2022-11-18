import xyz.zcraft.ACGPicDownload.Main;
public class Test {
    public static void main(String[] args) {
        // Main.main("-s dmoe".split(" "));
        // Main.main("--arg num=20 -o pic --multi-thread".split(" "));
        // Fetch f = new Fetch();
        // Source s=  new Source();
        // JSONObject json = new JSONObject();
        // json.put("tag", "tag1|tag21&tag22");
        // json.put("arg1", "val1");
        // json.put("arg3", "val3");
        // s.setUrl("http://someUrl?{arg3=$arg3}{unkown=$unkown}{&tag=$tag}{&arg1=$arg1}{&arg2=$arg2}");
        // s.setDefaultArgs(json);
        // f.replaceArgument(s);
        // System.out.println(s.getUrl());
        Main.main("--arg num=20,tag=\"萝莉&黑丝\" -o pic --multi-thread".split(" "));
    }
}
