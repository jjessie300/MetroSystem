import requests

API_KEY = '5ddb37a855ad5d2599f04ce3003c5047'
BASE_URL = 'https://api.themoviedb.org/3'

url = "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1"

headers = {
    "accept": "application/json",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1ZGRiMzdhODU1YWQ1ZDI1OTlmMDRjZTMwMDNjNTA0NyIsIm5iZiI6MTc1NDY3MzQ4OS44NjgsInN1YiI6IjY4OTYzMTUxOTBhNTIwOGE3ZTI2NzNlZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.eRwcf6gy1RK8JOn5nlyGuVOOr8BkkArJe0uUjcjkN-A"
}

response = requests.get(url, headers=headers)

print(response.text)


######

def fetch_now_playing(): 
    url = f'{BASE_URL}/movie/now_playing?api_key={API_KEY}'
    response = requests.get(url)
    if response.status_code == 200: 
        data = response.json() 
        print(data)
    else: 
        print('Error fetching data from TMDB: ', response.status_code)


fetch_now_playing() 