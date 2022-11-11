public class Main {
    public static void main(String[] args) {
        if(args.length == 0){
            usage();
        }

        switch(args[0]){
            case "-h","--help":{
                usage();
                break;
            }
            case "-v","--version":{

            }
            case "-u","--update-sources":{
                updateSources();
            }
        }
    }

    private static void updateSources() {

    }

    private static void usage() {

    }
}
