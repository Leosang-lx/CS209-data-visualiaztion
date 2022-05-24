# CS209A Project Report

**11912113 刘行**

**1191 刘仝**



## Architecture Design

The architecture of this project mainly consists of two parts, front-end and back-end.

### Data Collection

The data are accessed by `github.api` and using `jsoup`. With provided github APIs, we first accessed data about repositories with high stars in different languages (like java and python). Then, collect relevant issues and issue events (what happened to a certain issue) of the all repositories that we have got. To store the data, we use `PostgreSql` as database management system, which is also set as the data source of the query. 

#### Design of database

For example, the github repositories data is stored in table "github_repos_info" with columns:

("id", "name","full_name","html_url","language","created_at","stars","forks","watch","open_issues").

The issues data is stored in table "issue" with columns:

("id","repos_id","number","state","created_at","updated_at","closed_at")

The issue events data is stored in table "issue_event" with columns:

("id","issue_id","repos_id","event","created_at")

To store the topics of the repositories, since the relationship between repository and topics is a "many to many" relationship, so there is another table to store the topics.

...

While the front-end part is mainly for data visualization. And the source code of `html` page and the collected data (in `json`) is accessed by sending request to the back-end part.

(Front-end)

### Back-end

The back-end part consists of database and server using `springboot` as the framework. The database has already stored necessary data. The server will map different URLs to responses including pages in `html`, data in `json` and files in `javascript`. Therefore, it can process the request from the front-end. The data is stored in database and each time there is a request from the front-end, server will execute corresponding sql sentences in database and translate the data in json to the front-end. For the entity classes, the `springboot` framework can easily transform the data to json format.

#### API

This api is to get github repositories data from the database with some filter conditions.

![image-20220523154926101](C:\Users\Leosang\AppData\Roaming\Typora\typora-user-images\image-20220523154926101.png)

