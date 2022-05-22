public class restAPI {
    /**
     * add with following searching factors, at least one param is not null
     * @param language: program language
     * @param page: page number
     * @param per_page: max number of elements in one page
     * @param sort: sort by what
     * @param order: asc or desc
     * @return url in String with certain search conditions
     * */
    //api for searching repos with certain conditions
    public static String getSearchReposUrl(String language, int page, int per_page, String sort, String order){
        String github_repos_search = "https://api.github.com/search/repositories?q=language:java&page=1&per_page=10&sort=stars&order=desc";
        StringBuilder github_search_repos_sb = new StringBuilder(github_repos_search);
        if(language!=null){
            github_search_repos_sb.append("language:").append(language.toLowerCase());
            github_search_repos_sb.append('&');
        }
        if(page>0){
            github_search_repos_sb.append("page=").append(page);
            github_search_repos_sb.append('&');
        }
        else{
            github_search_repos_sb.append("page=1&");
        }
        if(per_page>0){
            github_search_repos_sb.append("per_page=").append(per_page);
            github_search_repos_sb.append('&');
        }
        else{
            github_search_repos_sb.append("per_page=30");
        }
        if(sort!=null){
            github_search_repos_sb.append("sort=").append(sort);
            github_search_repos_sb.append('&');
        }
        if(order!=null){
            github_search_repos_sb.append("order=").append(order);
        }
        return github_search_repos_sb.toString();
    }
    //api for searching information of a repos
    public static String getReposAPI(String reposFullName){
        return "https://api.github.com/repos/"+reposFullName;
    }
    //api for searching issues of a repos
    public static String getReposIssueAPI(String reposFullName, int page, int per_page, String state){
        String api = getReposAPI(reposFullName)+"/issues?state=%s&page=%d&per_page=%d";
        return String.format(api,state,page,per_page);
    }
    public static String getIssueEventsAPI(String reposFullName, int page, int per_page, String state){
        String api = getReposAPI(reposFullName)+"/issues/events?state=%s&page=%d&per_page=%d";
        return String.format(api,state,page,per_page);
    }
//    public static String
}
