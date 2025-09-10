from flask import Flask, render_template, redirect, url_for, request, session 
from werkzeug.security import generate_password_hash, check_password_hash
from flask_sqlalchemy import SQLAlchemy
import tmdb


app = Flask(__name__)
app.secret_key = 'your_secret_key'

# Configure SQL Alchemy 
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///users.db" 
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False 
db = SQLAlchemy(app) 

# Database model (single row within the db)
class User(db.Model): 
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(25), unique=True, nullable=False)
    password = db.Column(db.String(150), nullable=False)

    def set_password(self, password): 
        print("type in password " + password)
        self.password = generate_password_hash(password) 
        print("password hash is" + self.password)

    def check_password(self, password):
        return check_password_hash(self.password, password) # Returns a bool, is password entered correctly 
        


# Routes 
@app.route('/')
def home(): 
    return render_template('index.html')

@app.route('/login', methods=['GET','POST'])
def login():  
    if request.method == 'POST': 
        # Collect info from form 
        print("getting username")
        username = request.form['username']
        password = request.form["password"]
        user = User.query.filter_by(username=username).first() 

        # Check if username is in db and if password is correct 
        if user and user.check_password(password): 
            session['username'] = username # Create unique session to the user 
            return redirect(url_for('dashboard'))
        else: 
            print("Login failed: Invalid username or password")
            return render_template("login.html") # Back to login page 
    # Back to login page
    print("Put username and password to login") 
    return render_template("login.html")


@app.route("/register", methods=['GET','POST'])
def register(): 
    if request.method == 'POST': 
        print("getting new username")
        username = request.form['username']
        password = request.form["password"]
        password_conf = request.form["password_confirmation"]
        print(password)
        print(password_conf)
        user = User.query.filter_by(username=username).first()
        if user: # Check if user already in the db 
            print("Account already exists")
            return render_template("login.html", error="User already exists!")
        else:
            print("Account does not exist, create new one") 
            if password != password_conf: 
                print("Re-enter password, does not match")
                return render_template("register.html", error = "Passwords do not match")
            elif password == "": # Check if password and password conf left empty 
                print("Enter a password")
                return render_template("register.html", error = "Must enter a password")
            else: 
                print("Adding new account")
                new_user = User(username=username)
                new_user.set_password(password)
                db.session.add(new_user)
                db.session.commit() # Sending new user info to db 
                session['username'] = username 
                return redirect(url_for('dashboard'))
    return render_template("register.html")

@app.route("/dashboard")
def dashboard(): 
    return render_template("dashboard.html")

@app.route("/search", methods=['POST'])
def get_user_preferences(): 
    selected_genres = request.form.getlist("genre")
    selected_language = request.form["language"] # ONLY ALLOWS USER TO SELECT ONE LANGUAGE, IF NOT WONT WORK!!!
    #print(selected_language)

    results = tmdb.fetch_now_playing(selected_language, selected_genres)
    print(results)
    return render_template("search.html", user_movies=results)


if __name__ == "__main__": 
    with app.app_context(): 
        db.create_all()
    app.run(debug=True)


