package getData;

import java.io.*;
import java.util.*;
import java.sql.*;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.alibaba.fastjson.*;
import com.example.springproject.domain.GithubReposInfo;
import com.example.springproject.domain.UserEvent;
import org.jsoup.Jsoup;

import javax.validation.constraints.NotNull;

public class GetData {
    private static Map<String, String> headers = new HashMap<>();
    private static final String url = "jdbc:postgresql://localhost:5432/spring_project";
    private static final String user = "postgres";
    private static final String password = "Xing011006";

    static {
        try{
            File file = new File("D:/AAAAA/2022spring/Software Engineer/myGithubToken.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String token;
            token = br.readLine();
            br.close();
            fr.close();
            if(token!=null&&token.length()==40){
                System.out.println("Token read!");
                headers = new HashMap<>();
                headers.put("Authorization","token "+ token);
            }
            else{
                System.out.println("Token is empty!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getReposData(){
        String language = "java";
        String api = String.format("https://api.github.com/search/repositories?q=language:%s&sort=stars&page=1&per_page=100",language);
        String insert = "insert into github_repos_info values (%d,'%s','%s','%s','%s','%s',%d,%d,%d,%d);";
        try{
            org.jsoup.Connection.Response res = Jsoup.connect(api)
                    .ignoreContentType(true)
                    .headers(headers)
                    .execute();
//            System.out.println(res.body());
            JSONArray ans = JSON.parseArray(res.body());
            int len = ans.size();
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            for(int i = 0;i<len;i++){
                JSONObject json = ans.getJSONObject(i);
                stmt.executeUpdate(String.format(insert,
                        json.getInteger("id"),
                        json.getString("name"),
                        json.getString("full_name"),
                        json.getString("html_url"),
                        json.getString("language").toLowerCase(),
                        json.getString("created_at"),
                        json.getInteger("stargazers_count"),
                        json.getInteger("forks"),
                        json.getInteger("watchers"),
                        json.getInteger("open_issues")));
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getReposIssues(){
        String api = "https://api.github.com/repos/%s/issues?state=all&since=2022-04-18&page=%d&per_page=100";
        String select_template = "select id,full_name from github_repos_info";
        try{
            org.jsoup.Connection.Response res = null;
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            PreparedStatement pstmt = conn.prepareStatement("insert into issue values (?,?,?,?,?,?,?)");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(select_template);
            stmt.close();
            while(rs.next()){
                String full_name = rs.getString(2);
                int repos_id = rs.getInt(1);
                int page = 1;
                while(true){
                    res = Jsoup.connect(String.format(api,full_name,page++))
                            .ignoreContentType(true)
                            .headers(headers)
                            .execute();
                    int len = 0;
                    if(res.statusCode()==200){
                        JSONArray ans = JSON.parseArray(res.body());
                        len = ans.size();
                        for(int i = 0;i<len;i++){
                            JSONObject json = ans.getJSONObject(i);
                            pstmt.setInt(1,json.getInteger("id"));
                            pstmt.setInt(2,repos_id);
                            pstmt.setInt(3,json.getInteger("number"));
                            pstmt.setString(4,json.getString("state"));
                            pstmt.setString(5,json.getString("created_at"));
                            pstmt.setString(6,json.getString("updated_at"));
                            pstmt.setString(7,json.getString("closed_at"));
                            pstmt.executeUpdate();
                        }
                    }
                    else{
                        System.out.println(res.statusCode());
                    }
                    if(len<100){
                        break;
                    }
                }
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getIssueEvents(){
        String api = "https://api.github.com/repos/%s/issues/events?page=%d&per_page=100";
        String select_template = "select id,full_name from github_repos_info";
        String earliest = "2022-04-18";
        try{
            org.jsoup.Connection.Response res = null;
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(select_template);
            stmt.close();
            PreparedStatement pstmt = conn.prepareStatement("insert into issue_event values (?,?,?,?,?)");
            while(rs.next()){
                int repos_id = rs.getInt(1);
                String full_name = rs.getString(2);
                int page = 1;
                boolean before = false;
                while(true){
                    res = Jsoup.connect(String.format(api,full_name,page++))
                            .ignoreContentType(true)
                            .headers(headers)
                            .execute();
                    int len = 0;
                    if(res.statusCode()==200){
                        JSONArray ans = JSON.parseArray(res.body());
                        len = ans.size();
                        for(int i = 0;i<len;i++){
                            JSONObject json = ans.getJSONObject(i);
                            if(json.getString("created_at").substring(0,10).compareTo(earliest)>0){
                                before = true;
                                break;
                            }
                            pstmt.setLong(1,json.getLong("id"));
                            pstmt.setInt(2,json.getJSONObject("issue").getInteger("id"));
                            pstmt.setInt(3,repos_id);
                            pstmt.setString(4,json.getString("event"));
                            pstmt.setString(5,json.getString("created_at"));
                            pstmt.executeUpdate();
                        }
                    }
                    else{
                        System.out.println(res.statusCode());
                    }
                    if(before){
                        break;
                    }
                }
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getReposTopics(){
        String url = "https://api.github.com/repos/%s/topics";
        String select_template = "select id,full_name from github_repos_info;";
        try{
            org.jsoup.Connection.Response res = null;
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(select_template);
            stmt.close();
            PreparedStatement pstmt = conn.prepareStatement("insert into repos_topics values (?, ?)");
            while(rs.next()){
                int repos_id = rs.getInt(1);
                String full_name = rs.getString(2);
//                System.out.println(repos_id+" "+full_name);
                res = Jsoup.connect(String.format(url,full_name))
                        .ignoreContentType(true)
                        .headers(headers)
                        .execute();
                if(res.statusCode()==200){
                    JSONArray json = JSON.parseObject(res.body()).getJSONArray("name");
                    int len = json.size();
                    for(int i = 0;i<len;i++){
                        pstmt.setInt(1,repos_id);
                        pstmt.setString(2,json.getString(i));
                        pstmt.executeUpdate();
                    }
                }
                else{
                    System.out.println(res.statusCode());
                }
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Map<String, Object>> getTopicsFrequency(Integer limit){
        List<Map<String, Object>> lmso = new ArrayList<>();
        Integer num = limit!=null&&limit>0? limit : 20;
        String query = String.format("select a.topic, count(*) as cnt from (select topic from repos_topics) as a group by a.topic order by cnt desc limit %d;",num);
        try{
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Map<String, Object> mso = new HashMap<>();
                mso.put("name",rs.getString(1));
                mso.put("value",rs.getInt(2));
                lmso.add(mso);
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lmso;
    }

    public static Map<String, Object> analyseIssues(@NotNull String repos_name){
        Map<String, Object> msi = new HashMap<>();
        String query = String.format("select i.state,count(i.state) from issue i left join github_repos_info gri on gri.id = i.repos_id" +
                " where gri.name = '%s'" +
                " group by i.state;",repos_name);
        try{
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                msi.put(rs.getString(1),rs.getInt(2));
            }
            rs = stmt.executeQuery(String.format("select i.labeled, count(i.*) from issue i left join github_repos_info gri on gri.id = i.repos_id where gri.name = '%s' group by i.labeled;",repos_name));
            while(rs.next()){
                msi.put(rs.getString(1),rs.getInt(2));
            }
            rs = stmt.executeQuery(String.format("select avg(date_part('day',cast(to_date(substr(i.closed_at,1,10), 'YYYY-MM-DD') as TIMESTAMP) - cast(to_date(substr(i.created_at,1,10), 'YYYY-MM-DD') as TIMESTAMP))) from issue i left join github_repos_info gri on gri.id = i.repos_id where state = 'closed' and gri.name = '%s';",repos_name));
            rs.next();
            msi.put("avg_closed_time",rs.getDouble(1));
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msi;
    }

    public static List<GithubReposInfo> getUserRepos(String username, Integer limit){
        List<GithubReposInfo> lgri = new ArrayList<>();
        String url = String.format("https://api.github.com/users/%s/repos?per_page=%d",username,limit);
        try{
            org.jsoup.Connection.Response res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .headers(headers)
                    .execute();
            if(res.statusCode()==200){
                JSONArray ja = JSON.parseArray(res.body());
                int len = ja.size();
                for(int i = 0;i<len;i++){
                    JSONObject jo = ja.getJSONObject(i);
                    GithubReposInfo g = new GithubReposInfo();
                    g.setId(jo.getInteger("id"));
                    g.setFullName(jo.getString("full_name"));
                    g.setHtml_url(jo.getString("html_url"));
                    g.setLanguage(jo.getString("language"));
                    g.setCreated_at(jo.getString("created_at"));
                    g.setStars(jo.getInteger("stargazers_count"));
                    g.setForks(jo.getInteger("forks_count"));
                    g.setWatch(jo.getInteger("watchers_count"));
                    g.setOpen_issues(jo.getInteger("open_issues"));
                    lgri.add(g);
                }
            }
            else{
                System.out.println(res.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return lgri;
    }

    public static List<UserEvent> getUserEvents(String username, String from){
        List<UserEvent> lue = new ArrayList<>();
        int page = 1;
        String template = "https://api.github.com/users/%s/events?page=%d&per_page=100";
        boolean contin = true;
        try {
            while(contin){
                String url = String.format(template,username,page++);
                org.jsoup.Connection.Response res = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .headers(headers)
                        .execute();
                if (res.statusCode() == 200) {
                    JSONArray ja = JSON.parseArray(res.body());
                    int len = ja.size();
                    for(int i = 0;i<len;i++){
                        JSONObject jo = ja.getJSONObject(i);
                        String date = jo.getString("created_at");
                        if(date.substring(0,10).compareTo(from)<0){
                            contin = false;
                            break;
                        }
                        UserEvent ue = new UserEvent();
                        ue.setId(jo.getLong("id"));
                        ue.setType(jo.getString("type"));
                        ue.setActor_id(jo.getJSONObject("actor").getInteger("id"));
                        ue.setRepos_id(jo.getJSONObject("repo").getInteger("id"));
                        ue.setCreated_at(jo.getString("created_at"));
                        lue.add(ue);
                    }
                    if(len<100){
                        contin = false;
                    }
                }
                else{
                    System.out.println(res.statusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(lue.size());
        return lue;
    }

    public static List<String> getRecentReposLabels(String repos_name){
        List<String> ans = new ArrayList<>();
        String query = String.format("select distinct il.label from issue_label il left join issue i on il.issue_id = i.id" +
                " where i.repos_id = (select id from github_repos_info where name = '%s');",repos_name);
        try{
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                ans.add(rs.getString(1));
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static List<Map<String, Object>> getLabelFrequency(String repos_name){
        List<Map<String, Object>> lmso = new ArrayList<>();
        String query = String.format("select il.label,count(*) from issue_label il left join issue i on il.issue_id = i.id" +
                " where i.repos_id = (select id from github_repos_info where name = '%s')" +
                " group by il.label;",repos_name);
        try{
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Map<String, Object> mso = new HashMap<>();
                mso.put("name",rs.getString(1));
                mso.put("value",rs.getInt(2));
                lmso.add(mso);
            }
            stmt.close();
            conn.close();
    } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lmso;
    }
    public static void main(String[] args) throws Exception{
        Class.forName("org.postgresql.Driver");
        getReposData();
        getReposIssues();
        getIssueEvents();
        getReposTopics();
    }
}