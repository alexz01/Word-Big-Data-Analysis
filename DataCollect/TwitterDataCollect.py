# -*- coding: utf-8 -*-
"""
Created on Sun Apr  1 23:38:54 2018

@author: itsgu
"""
import requests
import base64
def getAPIkey(file='./data/twitter_api.key') :
    try:
        with open(file, 'r') as fp:
            key_str = fp.read()
            key = {}
            for key_item in key_str.split('\n'):
                key_part = key_item.split('\t')
                
                key[key_part[0]] = key_part[1]
            return key
    except Exception as e:
        print(e)

k = getAPIkey('./data/twitter.key')

twitter_search_url = 'https://api.twitter.com/1.1/search/tweets.json'

client_oauth_key_pair = '{}:{}'.format(k['consumer_key'],k['consumer_secret']).encode('ascii')
b64encode_client_oauth_key_pair = base64.b64encode(client_oauth_key_pair)
b64encode_client_oauth_key_pair = b64encode_client_oauth_key_pair.decode('ascii')

