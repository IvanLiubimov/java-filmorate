```mermaid
erDiagram
    USERS {
        int id PK
        varchar login
        varchar email
        varchar name
        date birthday
    }
    FILMS {
        int id PK
        varchar name
        varchar description
        date releaseDate
        int duration
        int rating_id FK
    }
    GENRES {
        int genre_id PK
        varchar name
    }
    FILMS_GENRES {
        int film_id FK
        int genre_id FK
    }
    FILM_LIKES {
        int id PK
        int user_id FK
        int film_id FK
    }
    FAVORITE_FILMS {
        int id PK
        int user_id FK
        int film_id FK
    }
    FRIENDSHIP {
        int id PK
        int user_id FK
        int friend_id FK
    }
    RATING {
        int id PK
        varchar name
    }

    USERS ||--o{ FILM_LIKES : likes
    USERS ||--o{ FAVORITE_FILMS : favorites
    USERS ||--o{ FRIENDSHIP : friends
    FILMS ||--o{ FILMS_GENRES : has_genre
    GENRES ||--o{ FILMS_GENRES : includes
    RATING ||--o{ FILMS : rated
```