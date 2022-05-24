package com.example.springproject.api;

import com.example.springproject.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
    @Query(
            value = "select i.* from" +
                    " issue i left join github_repos_info gri" +
                    " on gri.id = i.repos_id" +
                    " where gri.name = ?1" +
                    " and case when ?2<>'' then i.state = ?2 else 1=1 end" +
                    " and case when ?3<>'' then substr(i.created_at,1,10)> ?3 else 1=1 end",
            nativeQuery = true
    )
    List<Issue> getIssues(String repos_name, String state, String earliest);

}
