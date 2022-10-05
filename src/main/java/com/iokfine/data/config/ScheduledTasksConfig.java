package com.iokfine.data.config;

import com.iokfine.data.modules.premint.service.PremintPaser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class ScheduledTasksConfig {


    @Resource
    private PremintPaser premintPaser;

    @Scheduled(cron = "0 0 0/8 * * ?")
//    @Scheduled(cron = "*/60 * * * * ? ")
    void fetchPremintItem() {
        // 爬取项目
        premintPaser.catchProject();

        // 爬取详情
        premintPaser.catchMore();

    }




    private Request buildClientRequest(String url) {

        return new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("cookie", "_ga=GA1.1.655570109.1661927300; session_state=\"icYzMjaJOyzdkCl6K5MwGRsvVlAA/9h/EX2z/XK06YThUen4Vn1F5osKkE/bcqNu6kyhUqIFpFWooxgp6DqNW9NsPIkGI3tXWddEZ6FAJQ2V8hGwXkPwPbBJYVQKAAoKigo=:084a996c30224ed893cf9870e9d6e5ff0b73a1a0989f4869dbc274ca93392730:1662655410:f-DyUVTb3ihaQyfeq7r13D5zdIi-YHk6vh3FEivr8s0\"; SessionState=\"aEoPCsiU+SHgt57PwBnTiDP8Ljhwwli9TQmKscBji+H01wKKCMiP4IKP3pyfxQ1X7i7eTdGrUDD7BsdPZT2WXSjbpQjJUNuq5fv+bSbZ4fmZEL1CPaxP/neFxhMKAAoKKgo=:3246d4f9bb930200b80919848611a85ab602ccfc5a10a727ae25893cb9f51748:1664374750:NjoPYePSUiX8F5296Nd-PObYNBo2LOz8vxlRsvA0jM8\"; csrftoken=BAejjW2wdSMNfpEpSeFIuT5YWAVfquIejJmTn6KZytXkpkaSrNXhFrGkNfYce7Yr; session_id=ewu60dgn2dws1vaf9ey8exzk4iwe8nfb; __cf_bm=Lqm5ds3M_O_3IUPtA7211xhhjyr4U7upGXYzfwEIUVg-1664343248-0-AXeO7gN5idAX49ZgCG69YE/PVI8IVx9FXPxsbdcw5Z+yfSUzw21Pmucm7L1MbH7RdR/Rf9b1s5b4AuvxlLgooEc=; _ga_NMJ1VJK44S=GS1.1.1664343249.23.1.1664343322.0.0.0; csrftoken=BAejjW2wdSMNfpEpSeFIuT5YWAVfquIejJmTn6KZytXkpkaSrNXhFrGkNfYce7Yr; session_id=ewu60dgn2dws1vaf9ey8exzk4iwe8nfb; __cf_bm=NVGRGJJBOwYvpa5cB6oH7Op892j84DEJimTn50kQ7tE-1664344981-0-Aeav7Eu/nt+7LuX+e73t9Zpqtr5RgC5jpeXinT4/1kc+Kcqa6+k2TBhfwTxHhi4mv3+XZxLiuSRfpIjCYBQAyMo=")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .build();
    }

}
