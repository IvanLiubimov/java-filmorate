```mermaid
erDiagram
    users {
        INTEGER id PK
        VARCHAR login
        VARCHAR email
        VARCHAR name
        DATE birthday
    }
    films {
        INTEGER id PK
        VARCHAR name
        VARCHAR description
        DATE releaseDate
        INTEGER duration
        INTEGER rating_id FK
    }
    genres {
        INTEGER genre_id PK
        VARCHAR name
    }
    rating {
        INTEGER id PK
        VARCHAR name
    }
    directors {
        INTEGER id PK
        VARCHAR name
    }
    reviews {
        INTEGER review_id PK
        TEXT content
        BOOLEAN is_positive
        INTEGER user_id FK
        INTEGER film_id FK
        INTEGER useful
    }
    review_ratings {
        INTEGER review_id PK FK
        INTEGER user_id PK FK
        BOOLEAN is_positive
    }
    user_feeds {
        INTEGER event_id PK
        INTEGER user_id FK
        VARCHAR event_type
        VARCHAR operation
        INTEGER entity_id
        TIMESTAMP timestamp
    }
    film_likes {
        INTEGER user_id FK
        INTEGER film_id FK
    }
    favorite_films {
        INTEGER user_id FK
        INTEGER film_id FK
    }
    friendship {
        INTEGER user_id FK
        INTEGER friend_id FK
    }
    films_genres {
        INTEGER film_id FK
        INTEGER genre_id FK
    }
    films_directors {
        INTEGER film_id FK
        INTEGER director_id FK
    }

    users ||--o{ reviews : "writes"
    users ||--o{ review_ratings : "rates"
    users ||--o{ film_likes : "likes"
    users ||--o{ favorite_films : "favorites"
    users ||--o{ friendship : "friends"
    users }|--|| user_feeds : "triggers"

    films ||--o{ reviews : "has reviews"
    films ||--o{ films_genres : "has genres"
    films ||--o{ films_directors : "has directors"
    genres ||--o{ films_genres : "genres of"
    directors ||--o{ films_directors : "directs"

    rating ||--o{ films : "rated as"

    reviews ||--o{ review_ratings : "ratings"
```