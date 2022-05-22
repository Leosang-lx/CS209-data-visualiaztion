package com.example.springproject.api;
import com.example.springproject.domain.GithubReposInfo;
import com.example.springproject.domain.IssueEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GithubReposInfoRepository extends JpaRepository<GithubReposInfo, Integer>{
//    @Query("select r from GithubReposInfo r where r.language = ?1")
//    List<GithubReposInfo> getGithubReposInfosByLanguage(String lanuage);
    @Query(
            value = "select * from github_repos_info where case when (?1<>'') then language=?1 else 1=1 end" +
            " and case when (?2<>-1) then stars>=?2 else 1=1 end" +
            " and case when (?3<>'') then created_at>=?3 else 1=1 end" +
            " and case when (?4<>-1) then open_issues>=?4 else 1=1 end" +
            " limit ?5", nativeQuery = true)
    List<GithubReposInfo> getGithubReposInfos(
            String language,Integer least_stars,String since,Integer open_issues,Integer limit);
    List<GithubReposInfo> getGithubReposInfosByLanguage(String language);
    @Query(
            value = "select * from (select * from github_repos_info where id in (select repos_id from repos_topics where topic=?5)) as t" +
                    " where case when (?1<>'') then language=?1 else 1=1 end" +
                    " and case when (?2<>-1) then stars>=?2 else 1=1 end" +
                    " and case when (?3<>'') then created_at>=?3 else 1=1 end" +
                    " and case when (?4<>-1) then open_issues>=?4 else 1=1 end" +
                    " limit ?6", nativeQuery = true)
    List<GithubReposInfo> getGithubReposInfosWithTopic(
            String language,Integer least_stars,String since,Integer open_issues,String topic,Integer limit
    );
}
