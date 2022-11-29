package xyz.zcraft.acgpicdownload;

import xyz.zcraft.acgpicdownload.util.pixivutils.PixivDownloadUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

public class Test {
    private static String cookieString = "first_visit_datetime_pc=2022-06-12+23:43:48; p_ab_id=3; p_ab_id_2=2; p_ab_d_id=875235330; yuid_b=EHgjcXg; privacy_policy_notification=0; a_type=0; b_type=1; privacy_policy_agreement=5; __utmz=235335808.1667459879.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _fbp=fb.1.1667459881369.986054976; _im_vid=01GGY5N7AB5S2QZCCGD7RT5QVC; _im_uid.3929=h.649341fd7207ca36; login_ever=yes; _gcl_au=1.1.643642350.1667459998; tag_view_ranking=0xsDLqCEW6~-98s6o2-Rp~RTJMXD26Ak~EZQqoW9r8g~-n1sSUYIlS~QfGgFw-xlW~3kGxmFZ333~sAwDH104z0~ZldurqefWy~Ie2c51_4Sp~q3eUobDMJW~2R7RYffVfj~UBwhLy7Ngq~UD-63UkJba~QYP1NVhSHo~q_J28dYJ9d~AoKfsFwwdu~D9BseuUB5Z~pNtQi6YIt-~q303ip6Ui5~Bd2L9ZBE8q~_EOd7bsGyl~NW99fuIGG8~ziiAzr_h04~HBlflqJjBZ~IJkCuj9g6o~CrFcrMFJzz~tgP8r-gOe_~WWG1qXXsMj~MM6RXH_rlN~moGH48WbdM~w6HZJm4U_S~_bee-JX46i~P5-w_IbJrm~DADQycFGB0~yRLfwuMFlA~W4_X_Af3yY~Qa8ggRsDmW~CADtS59xDB~VRhhRnujTR~qx-Tlvmbdj~KMpT0re7Sq~gCB7z_XWkp~5dStmmRzKO~cQ_EXp9SAz~LfyX5eCTtL~4QveACRzn3~NTGKRMt6r7~yHYVzLZggf~leIwAgTj8E~LVSDGaCAdn~gpglyfLkWs~qWFESUmfEs~RcahSSzeRf~a4NwQM4c8N~jVtkOHXPKr~3cT9FM3R6t~2QTW_H5tVX~X_1kwTzaXt~ujS7cIBGO-~AI_aJCDFn0~GNtSSjSHgG~4TdWq8WRPd~fMqdKpgiU5~CWVPdZCkVB~jH0uD88V6F~xXhWS_7AGn~rNWcU0S3yo~DcYaANhRNt~K8esoIs2eW~enEDtQTJS4~8j8mF_R_KU~nP0KNyaoTX~f3hX16qgE6~GNcgbuT3T-~HY55MqmzzQ~DTDROgLuzO~RybylJRnhJ~8NpFhmNqI1~VHNHSrD3Tn~MUQoS0sfqG~uusOs0ipBx~jhuUT0OJva~1LN8nwTqf_~1bmFwrp_zN~Wxk4MkYNNf~kZyLZDtxMx~qtVr8SCFs5~yroC1pdUO-~lcBmqtXpZ4~SapL8yQw4Y~u3EAZmzDcl~hIbSsZ4_QS; adr_id=bAOXwAUAmvxD5z5FA5Jb7fxxoSEp20fK2sTJJ1SWozhYxhVn; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; device_token=e009ad9a666c98e76515b6ddcf8a194e; _gid=GA1.2.570721179.1669640617; __utma=235335808.404645431.1667459879.1669641032.1669729468.6; __utmc=235335808; __utmt=1; _gat_UA-1830249-3=1; _gat_gtag_UA_76252338_1=1; PHPSESSID=88458885_aMFgxzUciuCKvLUxBjsksxw4PerifTcP; _ga_MZ1NL4PHH0=GS1.1.1669729478.7.0.1669729484.0.0.0; c_type=22; __utmv=235335808.|2=login ever=yes=1^3=plan=normal=1^5=gender=male=1^6=user_id=88458885=1^9=p_ab_id=3=1^10=p_ab_id_2=2=1^11=lang=zh=1; __utmb=235335808.3.10.1669729468; __cf_bm=0QNTd6U.E3RHVjso8pR.qB.iOlA1ibmfGVlg9efpNJQ-1669729483-0-AeNnI52EiCiJRc8dUxuktBlN/JkgwT/uMzxKnv+A3rWZgSOiD6aV1Pzc/ManI2nAU7KviDW7MHPV+I89Hs1O2WnTDp1xf383tYqjHogdnYMYtcHVitVbSeeNHDsUxo3IpFUYfxLpOfmzWu+eo7fAyBWDNCdM7hfn5Ars03xn/cWEEQPk9zXazKGjDic7dYWGce21PP9s+m1PwlFaR16XrIc=; _ga_75BBYNYN9J=GS1.1.1669729469.9.1.1669729486.0.0.0; _ga=GA1.2.2000066063.1667459880; cto_bundle=kocI2l9uUDRNZWNCY3lsejdUYiUyRmNIaXBCMEphOEhCenlNRk1mcGZWbXgyeWpWbmoxdzFQNiUyRlZhelF1UzM0ejF2TGVNVEcwU0dWTU1FeUZma3FENHZxMmpIVGRwM1NuMm5vRzJIUFBsMUJjOGlyT0NXWCUyRlVkWXRqVktYVGZydXdUdCUyQktaNkpLRllKd1RLQ3VESUVkYXlMR0NQUSUzRCUzRA";

    public static void main(String[] args) throws IOException {
//        PixivArtwork a = new PixivArtwork();
//        a.setId("103156252");
//        a.setPageCount(123);
//        PixivDownload p = new PixivDownload(a);
//        new DownloadUtil(1).downloadPixiv(p, new File(""), new DownloadResult(),cookieString,"127.0.0.1", 7890);
//        System.setProperty("jdk.tls.useExtendedMasterSecret", "false");

        System.getProperties().put("proxySet", "true");
        System.getProperties().put("proxyHost", "127.0.0.1");
        System.getProperties().put("proxyPort", "7890");

        URL url = new URL("https://i.pximg.net/img-original/img/2022/11/28/00/01/24/103156252_p0.png");
        HttpsURLConnection c = (HttpsURLConnection) url.openConnection();

        c.setRequestProperty("referer", PixivDownloadUtil.REFERER);

        c.getInputStream();
    }
}
