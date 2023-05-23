from flask import Flask
from talkshow import blueprints, ext

PORT = 3001

def create_app(**kwargs):
    """Creates a new Flask app using the Factory Pattern"""
    app = Flask(__name__)
    app.config.update(kwargs)
    # extensions
    ext.configure(app)  # <-- registro dinâmico das extensões
    # blueprints
    blueprints.configure(app)  # <-- registro dinâmico dos blueprints
    return app


if __name__ == "__main__":
    app = create_app()
    app.run(port=PORT)