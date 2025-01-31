CREATE TABLE friendships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    current_user_id INT NOT NULL,
    friend_id INT NOT NULL,
    channel_id INT NOT NULL,
    UNIQUE (current_user_id, friend_id),
    FOREIGN KEY (current_user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id),
    FOREIGN KEY (channel_id) REFERENCES channels(id)
);
