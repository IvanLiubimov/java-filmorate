```mermaid
erDiagram
    USERS {
        INTEGER id PK
        VARCHAR login
        VARCHAR email
        VARCHAR name
        DATE birthday
    }
    FILMS {
        INTEGER id PK
        VARCHAR name
        VARCHAR description
        DATE releaseDate
        INTEGER duration
        INTEGER rating_id FK
    }
    GENRES {
        INTEGER genre_id PK
        VARCHAR name
    }
    DIRECTORS {
        INTEGER id PK
        VARCHAR name
    }
    RATINGS {
        INTEGER id PK
        VARCHAR name
    }
    REVIEWS {
        INTEGER review_id PK
        TEXT content
        BOOLEAN is_positive
        INTEGER user_id FK
        INTEGER film_id FK
        INTEGER useful
    }
    REVIEW_RATINGS {
        INTEGER review_id PK FK
        INTEGER user_id PK FK
        BOOLEAN is_positive
    }
    USER_FEEDS {
        INTEGER event_id PK
        INTEGER user_id FK
        VARCHAR event_type
        VARCHAR operation
        INTEGER entity_id
        TIMESTAMP timestamp
    }
    FILMS_GENRES {
        INTEGER film_id FK
        INTEGER genre_id FK
    }
    FILMS_DIRECTORS {
        INTEGER film_id FK
        INTEGER director_id FK
    }
    FILM_LIKES {
        INTEGER user_id FK
        INTEGER film_id FK
    }
    FAVORITE_FILMS {
        INTEGER user_id FK
        INTEGER film_id FK
    }
    FRIENDSHIP {
        INTEGER user_id FK
        INTEGER friend_id FK
    }
    
    USERS ||--o{ REVIEWS : "writes"
    USERS ||--o{ REVIEW_RATINGS : "rates"
    USERS ||--o{ FILM_LIKES : "likes"
    USERS ||--o{ FAVORITE_FILMS : "favorites"
    USERS ||--o{ FRIENDSHIP : "friends"
    FILMS ||--o{ REVIEWS : "reviews"
    FILMS ||--o{ FILMS_GENRES : "has genres"
    GENRES ||--o{ FILMS_GENRES : "genres of"
    FILMS ||--o{ FILMS_DIRECTORS : "has directors"
    DIRECTORS ||--o{ FILMS_DIRECTORS : "directs"
    RATINGS ||--o{ FILMS : "rated as"
```