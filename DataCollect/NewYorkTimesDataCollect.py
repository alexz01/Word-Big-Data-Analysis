# -*- coding: utf-8 -*-
"""
Created on Fri Mar 30 00:50:41 2018

@author: itsgu
"""

import requests
from bs4 import BeautifulSoup
import time
import json

def getAPIkey(file='./data/nyt_api.key') :
    try:
        with open(file) as fp:
            key = fp.read().strip()
            return key
    except Exception as e:
        print(e)


def searchNYTimes(api_key='', query='', fq='', 
                   fields='', sort='', begin_date='YYYYMMDD', 
                   end_date='YYYYMMDD', page=-1,):
    api_search_url= 'https://api.nytimes.com/svc/search/v2/articlesearch.json'

    if len(query) < 1:
        print('Query string is empty')
        return None
    
    fl_items = ['web_url',
    'snippet',
    'lead_paragraph',
    'abstract',
    'print_page',
    'blog',
    'source',
    'multimedia',
    'headline',
    'keywords',
    'pub_date',
    'document_type',
    'news_desk',
    'byline',
    'type_of_material',
    '_id',
    'word_count']
    
    search_param={'api-key':api_key,
                  'q':query }
    
    if len(fq) > 0 :
        search_param['fq'] = fq
        
    if len(fields) > 0:
        if set(fields).issubset(fl_items) :
            search_param['fl'] = fields
        else:
            print('Enter valid field values')
            return None
    if len(sort) > 0:
        if sort == 'newest' | sort == 'oldest':
            search_param['sort'] = sort
    
    if begin_date != 'YYYYMMDD':
        if int(begin_date[4:6]) > 0 & int(begin_date[4:6]) <= 12:
            if int(begin_date[6:9]) > 0 & int(begin_date[6:9]) <= 31:
                search_param['begin_date'] = begin_date
                
    if end_date != 'YYYYMMDD':
        if int(begin_date[4:6]) > 0 & int(begin_date[4:6]) <= 12:
            if int(begin_date[6:9]) > 0 & int(begin_date[6:9]) <= 31:
                search_param['end_date'] = end_date
    
    if page >= 0:
#        print('page is {}'.format(page))
        search_param['page'] = page
    
    try:
#        print('search params: {}'.format(search_param))
        resp = requests.get(url=api_search_url,params=search_param)
#        print(resp.text)
        print(resp.status_code)
        response_json = resp.json()
        resp.close()
    except Exception as e:
        print(e)
    if(response_json != None):
        return response_json
        

class NYTapiResponseWrapper:
    def __init__(self, response_json = {}):
        if len(response_json.keys()) > 0:
            self.status = response_json['status']
            self.copyright = response_json['copyright']
            self._response = response_json['response']
            self._parseResponse(self._response)
    
    def parseJSON(self, response_json = {} ):
        self.status = response_json['status']
        self.copyright = response_json['copyright']
        self._response = response_json['response']
        self._parseResponse(self._response)

    def _parseResponse(self, response):
        self._docs = response['docs']
        self._meta = response['meta']
        self._parseDocs(self._docs)
    
    def _parseDocs(self, docs):
        self.docs = []
        i = 0
        for doc_item in docs:
#            print(i)
            i += 1
            self.docs.append(Doc(doc = doc_item))
        
        
class Doc:
    def __init__(self, doc = {}):
        self._id = doc['_id']
        self.blog             = doc['blog']
        self.document_type    = doc['document_type']
        self.headline         = doc['headline']
        self.keywords         = doc['keywords']
        self.multimedia       = doc['multimedia']
        self.score            = doc['score']
        self.snippet          = doc['snippet']
        self.type_of_material = doc['type_of_material']
        self.web_url          = doc['web_url']
        self.word_count       = doc['word_count']


def getPageByURL(URL = ''):
    try:
        resp = requests.get(url=URL)
        soup = BeautifulSoup(resp.text, 'html.parser')
        resp.close()
        return soup
    except Exception as e:
        print(e)

def saveArticleText(headline, textParasSoup, filename):
    try:
        with open(filename, 'w') as fp:
            fp.write(headline)
            for para in textParasSoup:
                fp.write(para.text)
    except Exception as e:
        print(e)



if __name__ == '__main__':
    api_key = getAPIkey(file = './data/nyt.key')
    query = 'bitcoin'
    article_list = []
    article_loc = './data/NewsData/'
    for page in range(0,101):
        resp = searchNYTimes(api_key, query, fq='document_type:article', page=page)
        resp_ob = NYTapiResponseWrapper(resp)
        if len(resp_ob.docs) <= 0 : 
            break
        print(resp_ob._meta['offset'])
        for doc_item in resp_ob.docs:
            article = {'id':doc_item._id,'headline':doc_item.headline['main'], 'url':doc_item.web_url, 'downloaded':'N'}
            article_list.append(article)
        time.sleep(1)
        
    with open(article_loc+'article_list.json', 'w') as file:
        json.dump(article_list, file)

    for article in article_list:
        article_soup = getPageByURL(URL = article['url']) 
        print(article['id'], article['url'])
        paras = article_soup.find_all('p', 'story-body-text story-content')
        if(len(paras) < 2) :
            print("Not story-body-text")
            paras = article_soup.find_all('p', 'css-1xyeyil e2kc3sl0')
        if(len(paras) < 2):
            print("Not css-1xyeyil e2kc3sl0")
            paras = article_soup.find_all('p')
        with open(article_loc+article['id'], 'w', encoding='utf-8') as file:
            file.write(article['headline']+ '\n')
            article_text = ''
            for para in paras:
                article_text += para.text + '\n'
            file.write(article_text)
        article['downloaded']='Y'
        time.sleep(1)
        