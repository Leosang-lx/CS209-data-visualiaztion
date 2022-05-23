package getData;

import java.io.*;
import java.util.*;
import java.sql.*;

import com.alibaba.fastjson.*;
import org.jsoup.Jsoup;

import javax.validation.constraints.NotNull;

public class getData {
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

    public static Map<String, List<Object>> getTopicsFrequency(Integer limit){
        Map<String, List<Object>> msi = new HashMap<>();
        Integer num = limit!=null&&limit>0? limit : 10;
        String query = String.format("select a.topic, count(*) as cnt from (select topic from repos_topics) as a group by a.topic order by cnt desc limit %d;",num);
        try{
            List<Object> ls = new ArrayList<>();
            List<Object> li = new ArrayList<>();
            Connection conn = null;
            Statement stmt = null;
            conn = DriverManager.getConnection(url,user,password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                ls.add(rs.getString(1));
                li.add(rs.getInt(2));
            }
            msi.put("topic", ls);
            msi.put("frequency", li);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msi;
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

    public static void main(String[] args) throws Exception{
        Class.forName("org.postgresql.Driver");
        getReposData();
        getReposIssues();
        getIssueEvents();
        getReposTopics();
    }
}