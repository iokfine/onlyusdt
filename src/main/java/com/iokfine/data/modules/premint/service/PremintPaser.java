package com.iokfine.data.modules.premint.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.iokfine.data.modules.premint.dao.modal.PremintProject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author hjx
 * @date 2022/9/29
 */
@Service
@Slf4j
public class PremintPaser {

    private OkHttpClient client = new OkHttpClient().newBuilder().build();
    @Resource
    private PremintProjectService premintProjectService;

    public void catchProject() {
        List<String> tabs = Lists.newArrayList(
                "https://www.premint.xyz/collectors/explore/",
                "https://www.premint.xyz/collectors/explore/new/",
                "https://www.premint.xyz/collectors/explore/top/");
        for (String tab : tabs) {
            Request request = buildClientRequest(tab, "_ga=GA1.1.655570109.1661927300; session_state=\"icYzMjaJOyzdkCl6K5MwGRsvVlAA/9h/EX2z/XK06YThUen4Vn1F5osKkE/bcqNu6kyhUqIFpFWooxgp6DqNW9NsPIkGI3tXWddEZ6FAJQ2V8hGwXkPwPbBJYVQKAAoKigo=:084a996c30224ed893cf9870e9d6e5ff0b73a1a0989f4869dbc274ca93392730:1662655410:f-DyUVTb3ihaQyfeq7r13D5zdIi-YHk6vh3FEivr8s0\"; SessionState=\"DknnN7zdUJG7dTlBxZI+0wo6eegedRL7TdNCYNG9oXr1w/aKboMdHhKhLldw8S8GbGGLovcGZbK7Owno7ormcOU25Mku2w1V1XCTf2wKtfe9kyHtRDCnywS9o60KAAoKKgo=:3246d4f9bb930200b80919848611a85ab602ccfc5a10a727ae25893cb9f51748:1664899210:bPBNfhw40APdrJsdBG9SrcyHKlvSEBv-HHOBzCM_zBs\"; _fbp=fb.1.1664890841189.1209626707; _ga_4FWDNEZ861=GS1.1.1664893458.2.0.1664893460.0.0.0; _ga_Z4J5K83ZD6=GS1.1.1664894908.1.1.1664894941.0.0.0; __cf_bm=MUciXFSYKmk4FM969it6mhTLcnUJVvC_UUxM1ef0LuQ-1664895262-0-AcxfdckLemTmW3gc15SHeytDmMyoNtx6pxiGIwyUViHrNvd+ZH+bHJJB/c/1qqsehbFNVeJr1YNDi4I40ZtqH/8=; csrftoken=ZkWl22PH0hPwoSSTHtKLviSZfsrXb4b9e3RUXhSDkQt05NYPVAP9Zlhbq4TQlaWA; session_id=lrsqr8769yvzc28kgecytsqsey0fnke6; _ga_NMJ1VJK44S=GS1.1.1664888824.30.1.1664895538.0.0.0");
            try {
                Response response = client.newCall(request).execute();
                Document doc = Jsoup.parse(response.body().string());
                Elements elementsByClass = doc.getElementsByAttributeValueContaining("class", "d-flex strong border-left border-right border-bottom p-3");
                elementsByClass.forEach(element -> {
                    // 先判断可以注册不 通过注册按钮
                    Elements aTags = element.getElementsByTag("a");
                    if (aTags.size() > 1) {
                        PremintProject premintProject = new PremintProject();
                        // logo设置
                        String logo = element.getElementsByTag("img").get(0).attr("src");
                        premintProject.setLogo(logo);

                        // 设置项目名
                        premintProject.setName(aTags.get(0).text());
                        premintProject.setUrl(aTags.get(0).attr("href").replaceAll("/", ""));
                        // 供应量
                        Element element1 = aTags.get(0).nextElementSibling();
                        premintProject.setSupply(element1.text().replace(" Supply", ""));

                        // 钱包余额
                        Elements mintDate_price = element.getElementsByAttributeValueContaining("class", "col-lg-2 offset-3 offset-md-0 my-2 my-sm-0");
                        premintProject.setPrice(mintDate_price.get(1).text());
                        // mint日期
                        premintProject.setMintDate(mintDate_price.get(0).text());
                        String tabName = "trending";
                        if (tab.contains("new")) {
                            tabName = "newest";
                        } else if (tab.contains("top")) {
                            tabName = "top";
                        }
                        premintProject.setTab(tabName);
                        premintProjectService.save(premintProject);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("处理完毕 {} ", tab);
        }
    }

    public void catchMore() {
        // 爬取详情
        List<PremintProject> topList = premintProjectService.getAll();
        topList.forEach(topItem -> {
            PremintProject premintProject = new PremintProject();
            BeanUtil.copyProperties(topItem, premintProject);
            String projectUrl = "https://www.premint.xyz/" + topItem.getUrl();
            Request request = buildClientRequest(projectUrl, "");
            try {

                Response response = client.newCall(request).execute();
                log.info("--------------------------------------");
                log.info(projectUrl);
                paser(response.body().string(), premintProject);
                log.info("--------------------------------------");
                System.out.println();
            }catch (Exception e){
                log.error(e.getMessage());
            }
            premintProjectService.save(premintProject);
        });
    }

    public static void main(String[] args) {
        PremintProject premintProject = new PremintProject();
        paser(FileUtil.readUtf8String("D:\\JetBrains\\workspace\\onlyusdt\\sss.html"), premintProject);
    }

    public static void paser(String html, PremintProject premintProject) {
        try {
            Document doc = Jsoup.parse(html);
            // windner
            Elements number_of_winners = doc.getElementsContainingText("Number of Winners ");
            if (CollectionUtil.isNotEmpty(number_of_winners)) {
                System.out.println("Spots");
                System.out.println(number_of_winners.get(13).getElementsByTag("span").get(0).text().replace(" Spots", ""));
                premintProject.setWinners(number_of_winners.get(13).getElementsByTag("span").get(0).text().replace(" Spots", ""));
            }
            // close date
            Elements registration_closes = doc.getElementsContainingText("Registration Closes");
            if (CollectionUtil.isNotEmpty(registration_closes)) {
                System.out.println("Registration Closes");
                System.out.println(registration_closes.get(13).getElementsByTag("span").get(0).text());
                premintProject.setRegDeadline(registration_closes.get(13).getElementsByTag("span").get(0).text());
            }
            // close date
            Elements docElementsCdark = doc.getElementsByClass("c-dark");
            if (CollectionUtil.isNotEmpty(docElementsCdark)) {
                System.out.println("ETH Need");
                System.out.println(docElementsCdark.get(1).text().replace(" ETH", ""));
                premintProject.setRequirementETH(docElementsCdark.get(1).text().replace(" ETH", ""));
            }

            Element aClass = doc.getElementsByAttributeValueContaining("class", "card z-depth-2-top rounded-xl no-border overflow-hidden").get(0);
            List<String> types = Lists.newArrayList();
            // tw
            Elements twitter = aClass.getElementsContainingOwnText("Twitter");
            if (CollectionUtil.isNotEmpty(twitter)) {
//                System.out.println("twitter");
                types.add("twitter");
            }
            // discord
            Elements discord = aClass.getElementsContainingOwnText("Discord");
            if (CollectionUtil.isNotEmpty(discord)) {
//                System.out.println("discord");
                types.add("discord");
            }
            // email
//            Elements email = aClass.getElementsContainingOwnText("Email");
//            if (CollectionUtil.isNotEmpty(email)) {
////                System.out.println("email");
//                types.add("email");
//            }
            String join = CollectionUtil.join(types, ",");
            premintProject.setType(join);

            System.out.println(join);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private Request buildClientRequest(String url, String cookie) {

        return new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("cookie", cookie)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .build();
    }
}
