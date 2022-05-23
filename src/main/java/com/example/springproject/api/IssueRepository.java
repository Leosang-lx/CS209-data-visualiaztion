package com.example.springproject.api;

import com.example.springproject.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
//    @Query(
//            value = "select * from "
//    )
//    List<Issue>
}
