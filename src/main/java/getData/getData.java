package getData;
import java.io.*;
public class getData {
    private static String access_token;
    private static int read_token(){
        try{
            File file = new File("D:/AAAAA/2022spring/Software Engineer/myGithubToken.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            line = br.readLine();
            br.close();
            fr.close();
            if(line!=null&&line.length()==40){
                access_token = line;
                System.out.println("Token read!");
                return 1;
            }
            else{
                System.out.println("Token is empty!");
                return 0;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    public void getReposData(){
        String language = "java";
        String url = "https://api.github.com/search/repositories?q=language:%s&sort=stars&page=1&per_page=100";
        String insert = "insert into github_repos_info values ({},'{}','{}','{}','{}','{}',{},{},{},{});";
    }
    public static void main(String[] args) {
        read_token();

    }
}
