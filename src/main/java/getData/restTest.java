package getData;

import com.alibaba.fastjson.*;
import org.jsoup.*;

import java.util.*;

public class restTest {
    //test
    public void githubReposSearchTest(){
        try{
            String github_repos_search =
                    restAPI.getSearchReposUrl(null,1,30,null,null);
            System.out.println(github_repos_search);
            Connection.Response res = Jsoup.connect(github_repos_search)
                    .ignoreContentType(true)
                    .execute();
//            JSONObject ans = JSON.parseObject(res.body());
            System.out.println(res.body());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    //test
    public void issueEventsFromReposTest(){
        try{
            String url = restAPI.getIssueEventsAPI("languagetool-org/languagetool",1,1,"closed");
            System.out.println(url);
            Connection.Response res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .execute();
            JSONArray ans = JSON.parseArray(res.body());
            System.out.println(res.body());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getReposIssues(){
        try{
            String url =
                    restAPI.getReposIssueAPI("languagetool-org/languagetool",1,10,"closed");
            System.out.println(url);
            Connection.Response res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .execute();
//            JSONObject ans = JSON.parseObject(res.body());
            System.out.println(res.body());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reposAPI(){
        try{
            Connection.Response res = Jsoup.connect(restAPI.getReposAPI("languagetool-org/languagetool"))
                    .ignoreContentType(true)
                    .execute();
            System.out.println(res.body());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        restTest rt = new restTest();
        rt.getReposIssues();
    }
}
