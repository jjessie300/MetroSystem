import requests 

API_KEY = '5ddb37a855ad5d2599f04ce3003c5047'
BASE_URL = 'https://api.themoviedb.org/3'

genre_map = {
    "Action": 28, 
    "Adventure": 12, 
    "Animation": 16, 
    "Comedy": 35, 
    "Crime": 80, 
    "Documentary": 99, 
    "Drama": 18, 
    "Family": 10751,
    "Fantasy": 14,
    "History": 36,
    "Horror": 27,
    "Music": 10402,
    "Mystery": 9648,
    "Romance": 10749,
    "Science Fiction": 878,
    "TV Movie": 10770,
    "Thriller": 53,
    "War": 10752,
    "Western": 37
}

#language = input("What language movie are you looking for? ")
#selected_genres = ["Comedy", "Animation"]

# language = None 
# selected_genres = []


# Function to fetch now playing movies based on user preferences using the API 
def fetch_now_playing(language, selected_genres): 
    url = f'{BASE_URL}/movie/now_playing?api_key={API_KEY}'

    response = requests.get(url)
    data = response.json()

    filtered_movies = []

    for movie in data["results"]:
        # Check language 
        if language == None or movie["original_language"] == language: 
            #print(movie["original_language"])
    
            # Check genres
            if selected_genres == [] or check_genres(get_genre_ids(selected_genres), movie): 
                #print(check_genres(get_genre_ids(selected_genres), movie))
                #print(movie["id"])
                #print(movie["genre_ids"])
                filtered_movies.append(movie["title"])
    
    #print(filtered_movies)
    return filtered_movies 


def get_genre_ids(selected_genres): 
    genre_ids = []
    for genre in selected_genres: 
        genre_ids.append(genre_map[genre])
    #print(genre_ids)
    return genre_ids

# Check if any of the selected genres apply 
def check_genres(genre_ids, movie): 
    for id in genre_ids: 
        for movie_genre in movie["genre_ids"]: 
            if id == movie_genre: 
                return True 
           
    return False 


# fetch_now_playing(language, selected_genres) 
