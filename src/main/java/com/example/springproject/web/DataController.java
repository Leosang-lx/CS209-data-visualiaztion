package com.example.springproject.web;

import getData.GetData;
import com.example.springproject.api.IssueRepository;
import com.example.springproject.domain.Issue;
import com.example.springproject.domain.UserEvent;
import com.example.springproject.api.GithubReposInfoRepository;
import com.example.springproject.api.IssueEventRepository;
import com.example.springproject.domain.GithubReposInfo;
import com.example.springproject.domain.IssueEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
public class DataController {
    @Autowired
    GithubReposInfoRepository githubReposInfoRepository;
    @Autowired
    IssueEventRepository issueEventRepository;
    @Autowired
    IssueRepository issueRepository;

    @GetMapping("/allRepos")
    @CrossOrigin
    public List<GithubReposInfo> getAllRepos(){
        return githubReposInfoRepository.findAll();
    }

    @GetMapping("/repos/{repos_id}")
    @CrossOrigin
    public GithubReposInfo getReposById(@PathVariable(value = "repos_id")Integer repos_id){
        return githubReposInfoRepository.getById(repos_id);
    }

//    @GetMapping("/reposInfo")
//    @CrossOrigin
//    public List<GithubReposInfo> getGithubReposByLanguage(@RequestParam(value = "language", required = false) String language){
//        if(language==null){
//
//        }
//        return githubReposInfoRepository.getGithubReposInfosByLanguage(language);
//    }
    @GetMapping("/filterRepos")
    @CrossOrigin
    public List<GithubReposInfo> getGithubRepos(
            @RequestParam(value="language", required = false)@Nullable String language,
            @RequestParam(value="stars", required = false)@Nullable Integer least_stars,
            @RequestParam(value="since", required = false)@Nullable String since,
            @RequestParam(value="open_issues", required = false)@Nullable Integer open_issues,
            @RequestParam(value="topic",required = false)@Nullable String topic,
            @RequestParam(value="limit", required = false)@Nullable Integer limit){
        if(topic==null){
            return githubReposInfoRepository.getGithubReposInfos(
                    language!=null?language:"",
                    least_stars!=null?least_stars:-1,
                    since!=null?since:"",
                    open_issues!=null?open_issues:-1,
                    limit!=null?limit:30);
        }
        else{
            return githubReposInfoRepository.getGithubReposInfosWithTopic(
                    language!=null?language:"",
                    least_stars!=null?least_stars:-1,
                    since!=null?since:"",
                    open_issues!=null?open_issues:-1,
                    topic,
                    limit!=null?limit:30
            );
        }
    }

    @GetMapping("/issueEvents")
    @CrossOrigin
    public Map<String, List<Object>> getIssueEvents(
            @RequestParam(value="repos_name") String repos_name,
            @RequestParam(value="begin",required = false)@Nullable String begin,
            @RequestParam(value="end", required = false)@Nullable String end,
            @RequestParam(value="events",required = false)@Nullable String events
    ){
        List<IssueEvent> lie = issueEventRepository.getIssueEvents(
                repos_name,
                begin!=null?begin:"",
                end!=null?end:"",
                events!=null?events:""
        );

        HashMap<String, Integer> msi = new HashMap<>();
        for(IssueEvent ie:lie){
            String date = ie.getCreated_at().substring(0,10);
            if(msi.containsKey(date)){
                msi.put(date, msi.get(date)+1);
            }
            else{
                msi.put(date,1);
            }
        }
        LocalDate before;
        LocalDate last;
        if(end==null){
            if(begin!=null){
                before = LocalDate.parse(begin);
                last = before.plusDays(30);
            }
            else{
                last = LocalDate.now();
                before = last.minusDays(30);
            }
        }
        else{
            last = LocalDate.parse(end);
            if(begin==null){
                before = last.minusDays(30);
            }
            else{
                before = LocalDate.parse(begin);
            }
        }
        List<Object> dates = new ArrayList<>();
        List<Object> nums = new ArrayList<>();
        dates.add(before.toString());
        Integer num = msi.get(before.toString());
        nums.add(Objects.requireNonNullElse(num, 0));
        int cnt = 1;
        while(true){
            LocalDate date = before.plusDays(cnt++);
            if(date.compareTo(last)<=0){
                String s = date.toString();
                dates.add(s);
                num = msi.get(s);
                if(num==null){
                    num = 0;
                }
                nums.add(num);
            }
            else{
                break;
            }
        }
        Map<String, List<Object>> msl = new HashMap<>();
        msl.put("dates",dates);
        msl.put("nums",nums);
        return msl;
    }

    @GetMapping("/userEvents")
    @CrossOrigin
    public Map<String, List<Object>> getUserEvents(
            @RequestParam(value="username") String username,
            @RequestParam(value="begin",required = false)@Nullable String begin,
            @RequestParam(value="end", required = false)@Nullable String end,
            @RequestParam(value="events",required = false)@Nullable String events
    ){
        LocalDate before;
        LocalDate last;
        if(end==null){
            if(begin!=null){
                before = LocalDate.parse(begin);
                last = before.plusDays(30);
            }
            else{
                last = LocalDate.now();
                before = last.minusDays(30);
            }
        }
        else{
            last = LocalDate.parse(end);
            if(begin==null){
                before = last.minusDays(30);
            }
            else{
                before = LocalDate.parse(begin);
            }
        }
        List<UserEvent> lie = GetData.getUserEvents(
                username,
                before.toString()
//                end!=null?end:"",
//                events!=null?events:""
        );

        HashMap<String, Integer> msi = new HashMap<>();
        for(UserEvent ie:lie){
            String date = ie.getCreated_at().substring(0,10);
            if(msi.containsKey(date)){
                msi.put(date, msi.get(date)+1);
            }
            else{
                msi.put(date,1);
            }
        }
        List<Object> dates = new ArrayList<>();
        List<Object> nums = new ArrayList<>();
        dates.add(before.toString());
        Integer num = msi.get(before.toString());
        nums.add(Objects.requireNonNullElse(num, 0));
        int cnt = 1;
        while(true){
            LocalDate date = before.plusDays(cnt++);
            if(date.compareTo(last)<=0){
                String s = date.toString();
                dates.add(s);
                num = msi.get(s);
                if(num==null){
                    num = 0;
                }
                nums.add(num);
            }
            else{
                break;
            }
        }
        Map<String, List<Object>> msl = new HashMap<>();
        msl.put("dates",dates);
        msl.put("nums",nums);
        return msl;
    }

    @GetMapping("/topicsFrequency")
    @CrossOrigin
    public Map<String, List<Object>> topicsFrequency(@RequestParam(value = "limit", required = false) Integer limit){
        return GetData.getTopicsFrequency(limit);
    }

    @GetMapping("/{repos_name}/issues")
    @CrossOrigin
    public List<Issue> getIssues(@PathVariable(value = "repos_name")@NotNull String repos_name,
                                 @RequestParam(value = "state",required = false)@Nullable String state,
                                 @RequestParam(value = "earliest",required = false)@Nullable String earliest){
        return issueRepository.getIssues(
                repos_name,
                (state==null||state.equals("all"))? "" : state,
                earliest==null? "" : earliest);
    }

    @GetMapping("/{repos_name}/issues_num")
    @CrossOrigin
    public Map<String, Object> getReposIssuesNum(@PathVariable(value = "repos_name")@NotNull String repos_name){
        Map<String, Object> msi = GetData.analyseIssues(repos_name);
        if(!msi.containsKey("t")){
            msi.put("t",0);
        }
        if(!msi.containsKey("f")){
            msi.put("f",0);
        }
        if(!msi.containsKey("open")){
            msi.put("open",0);
        }
        if(!msi.containsKey("closed")){
            msi.put("closed",0);
        }
        return msi;
    }

    @GetMapping("/{username}/list_repositories")
    @CrossOrigin
    public List<GithubReposInfo> getUserRepos(
            @PathVariable(value = "username")@NotNull String username,
            @RequestParam(value = "limit", required = false) Integer limit){
        return GetData.getUserRepos(username, limit==null||limit<=0||limit>100? 30:limit);
    }

    @GetMapping("/{username}/userevents")
    @CrossOrigin
    public List<UserEvent> getUserEvents(
            @PathVariable(value = "username")@NotNull String username,
            @RequestParam(value = "since",required = false) String since){
        LocalDate thirtyBefore = LocalDate.now().minusDays(30);
        if(since==null||LocalDate.parse(since).compareTo(thirtyBefore)<0){
            return GetData.getUserEvents(username,thirtyBefore.toString());
        }
        return GetData.getUserEvents(username,since);
    }

    @RequestMapping("/download/{filetype}/{filename}")
    @CrossOrigin
    public String downloadFiles(
            @PathVariable("filetype") String filetype,
            @PathVariable("filename") String filename,
            HttpServletResponse response) throws Exception{
        String path = "D:\\AAAAA\\2022spring\\Java2\\project\\CS209-data-visualiaztion\\src\\main\\resources\\static\\%s\\%s";
        File file = new File(String.format(path,filetype,filename));
        if (file.exists()) {
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setContentLengthLong(file.length());
            response.setHeader("Content-Disposition","attachment;fileName="+URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = 0;
                while ((i = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, i);
                    os.flush();
                }
                System.out.println("Download file successfully!");
            }
            catch (Exception e) {
                System.out.println("Download file failed!");
            }
            finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "Download successfully!";
        }
        return "File not found!";
    }
}
