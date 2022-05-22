package com.example.springproject.api;

import com.example.springproject.domain.IssueEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueEventRepository extends JpaRepository<IssueEvent, Long> {

    @Query(
            value = "select ie.* from" +
                    " issue_event ie left join github_repos_info gri" +
                    " on gri.id = ie.repos_id" +
                    " where gri.name = ?1" +
                    " and case when (?2<>'') then substr(ie.created_at,1,10) >= ?2 else 1=1 end" +
                    " and case when (?3<>'') then substr(ie.created_at,1,10) <= ?3 else 1=1 end" +
                    " and case when (?4<>'') then ie.event in (select * from regexp_split_to_table(?4,',')) else 1=1 end;", nativeQuery = true
    )
    List<IssueEvent> getIssueEvents(
            String repos_name, String begin, String end, String events);
}
