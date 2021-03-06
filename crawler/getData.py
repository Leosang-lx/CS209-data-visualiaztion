import datetime
import requests
import psycopg2
import json
import requests
import datetime


file = open('D:/AAAAA/2022spring/Software Engineer/myGithubToken.txt','r')
token = file.readline()
file.close()
# print(token)

headers={"Authorization":'token '+ token,"Accept":"application/vnd.github.v3+json"}
time = datetime.datetime.now()
deltaTime = datetime.timedelta(days=30)
# print(time.strftime('%Y-%m-%dT%H:%M:%SZ'))
since = (time-deltaTime).strftime('%Y-%m-%dT%H:%M:%SZ')
# a = '2020-01-01T00:00:00Z'
# b = '2020-01-02T00:00:00Z'
# print(a<b)

def getReposData():  
    language = 'python'
    url = 'https://api.github.com/search/repositories?q=language:{}&sort=stars&page=1&per_page=100'.format(language)
    sql = "insert into github_repos_info values ({},'{}','{}','{}','{}','{}',{},{},{},{});"
    r = requests.get(url,headers=headers)
    if r.status_code==200:
        items = r.json()['items']
        for i in items:
            s = sql.format(i['id'],
            i['name'],
            i['full_name'],
            i['html_url'],
            i['language'].lower(),
            i['created_at'],
            i['stargazers_count'],
            i['forks'],
            i['watchers'],
            i['open_issues'])
            print(s)
            curr.execute(s)
    else:
        print(r.status_code)

def getReposIssues():
    language = 'python'
    url = 'https://api.github.com/repos/{}/issues?state=all&since={}&page={}&per_page={}'
    select_template = "select id,full_name from github_repos_info where language='{}';".format(language) # select all repos and find issues
    # (id, html_url, number, state, created_at, updated_at, closed_at)
    insert_template = "insert into issue values({}, {}, {}, '{}', '{}', '{}', '{}',{});"
    insert_label = "insert into issue_label values ({},'{}');"
    try:
        curr.execute(select_template)
    except Exception as e:
        print(e)
    else:
        rows = curr.fetchall()
    # print(rows)
    for i in rows:
        reposId, full_name = i
        print(reposId, full_name)
        page = 1
        per_page = 100
        while 1:
            searchIssue = url.format(full_name,since,page,per_page)
            print(full_name,page)
            page+=1
            r = requests.get(searchIssue, headers=headers)
            # print(r.content)
            # break
            if r.status_code==200:
                for issue in r.json():
                    issue_id = issue['id']
                    if len(issue['labels'])>0:
                        labeled = 'true'
                    else:
                        labeled = 'false'
                    insert = insert_template.format(issue['id'],reposId,issue['number'],issue['state'],issue['created_at'],issue['updated_at'],issue['closed_at'],labeled)
                    curr.execute(insert)
                    if labeled:
                        for label in issue['labels']:
                            name = label['name']
                            while "'" in name:
                                idx = name.index('\'')
                                name = name[:idx]+name[idx+1:]
                            curr.execute(insert_label.format(issue_id,name))
                    
            else:
                print(r.status_code)
            if len(r.json())<100:
                break

def getIssueEvents():
    language = 'python'
    url = 'https://api.github.com/repos/{}/issues/events?page={}&per_page={}'
    select_template = "select id,full_name from github_repos_info where language = '{}';".format(language) # select all repos and find issues
    # (id, html_url, number, state, created_at, updated_at, closed_at)
    insert_template = "insert into issue_event values({},{},{},'{}','{}');"
    try:
        curr.execute(select_template)
    except Exception as e:
        print(e)
    else:
        rows = curr.fetchall()
    # print(rows)
    for i in rows:
        reposId, full_name = i
        print(reposId, full_name)
        page = 1
        per_page = 100
        before = False
        while 1:
            searchEvent = url.format(full_name,page,per_page)
            # print(searchEvent)
            print(full_name,page)
            page+=1
            r = requests.get(searchEvent, headers=headers)
            if r.status_code==200:
                for event in r.json():
                    if event['created_at']<since:
                        before = True
                        break
                    insert = insert_template.format(event['id'],event['issue']['id'],reposId,event['event'],event['created_at'])
                    try:
                        curr.execute(insert)
                    except Exception as e:
                        print(e)
            else:
                print(r.status_code)
            if before:
                break

def getReposTopics():
    language = 'python'
    url = 'https://api.github.com/repos/{}/topics'
    select_template = "select id,full_name from github_repos_info  where language = '{}';".format(language) # select all repos and find issues
    # (id, html_url, number, state, created_at, updated_at, closed_at)
    insert_template = "insert into repos_topics values({},'{}');"
    try:
        curr.execute(select_template)
    except Exception as e:
        print(e)
    else:
        rows = curr.fetchall()
    for i in rows:
        reposId, full_name = i
        print(reposId, full_name)
        searchTopic = url.format(full_name)
        r = requests.get(searchTopic, headers=headers)
        if r.status_code==200:
            names = r.json()['names']
            for topic in names:
                insert = insert_template.format(reposId,topic)
                curr.execute(insert)
        else:
            print(r.status_code)

# def getReposLabels():
#     url = 'https://api.github.com/search/labels?repository_id=22790488&&page=1&per_page=100'

conn = psycopg2.connect(database="spring_project", user="postgres", password="Xing011006", host="127.0.0.1", port="5432")
curr = conn.cursor()
# getReposData()
# getReposIssues()
getIssueEvents()
# getReposTopics()

curr.close()
conn.commit()
conn.close()