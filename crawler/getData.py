import datetime
from socket import NI_MAXSERV
import requests
import psycopg2
import json
import requests
import datetime


file = open('D:/AAAAA/2022spring/Software Engineer/myGithubToken.txt','r')
token = file.readline()
file.close()
# print(token)

headers={"Authorization":token}
time = datetime.datetime.now()
deltaTime = datetime.timedelta(days=30)
# print(time.strftime('%Y-%m-%dT%H:%M:%SZ'))
since = (time-deltaTime).strftime('%Y-%m-%dT%H:%M:%SZ')
# a = '2020-01-01T00:00:00Z'
# b = '2020-01-02T00:00:00Z'
# print(a<b)

def getReposData():  
    language = 'java'
    url = 'https://api.github.com/search/repositories?q=language:{}&sort=stars&page=1&per_page=100&token=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'.format(language)

    r = requests.get(url)
    if r.status_code==200:
        return r.json()['items']
    else:
        return 'null'

def getReposIssues():
    language = 'java'
    url = 'https://api.github.com/repos/{}/issues?state=all&since={}&page={}&per_page={}'
    select_template = "select id,full_name from github_repos_info where language='{}';".format(language) # select all repos and find issues
    # (id, html_url, number, state, created_at, updated_at, closed_at)
    insert_template = "insert into issue values({}, {}, {}, '{}', '{}', '{}', '{}');"
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
            if r.status_code==200:
                for issue in r.json():
                    insert = insert_template.format(issue['id'],reposId,issue['number'],issue['state'],issue['created_at'],issue['updated_at'],issue['closed_at'])
                    curr.execute(insert)
            else:
                print(r.status_code)
            if len(r.json())<100:
                break

def getIssueEvents():
    url = 'https://api.github.com/repos/{}/issues/events?page={}&per_page={}'
    select_template = 'select id,full_name from github_repos_info;' # select all repos and find issues
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
                    # try:
                    curr.execute(insert)
                    # except Exception as e:
                    #     print(e)
            else:
                print(r.status_code)
            if before:
                break

def getReposTopics():
    url = 'https://api.github.com/repos/{}/topics'
    select_template = 'select id,full_name from github_repos_info;' # select all repos and find issues
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

conn = psycopg2.connect(database="spring_project", user="postgres", password="Xing011006", host="127.0.0.1", port="5432")
curr = conn.cursor()
# getReposIssues()
# getIssueEvents()
# getReposTopics()

# sql = "insert into github_repos_info values ({},'{}','{}','{}','{}','{}',{},{},{},{});"
# for i in items:
#     s = sql.format(i['id'],
#     i['name'],
#     i['full_name'],
#     i['html_url'],
#     i['language'].lower(),
#     i['created_at'],
#     i['stargazers_count'],
#     i['forks'],
#     i['watchers'],
#     i['open_issues'])
#     print(s)
#     curr.execute(s)

# items = getReposData()
# print(items)

curr.close()
conn.commit()
conn.close()