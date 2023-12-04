#DROP DATABASE IF EXISTS spoti_api_js_php_playlist;
#CREATE DATABASE IF NOT EXISTS spoti_api_js_php_playlist;
USE spoti_api_js_php_playlist;
#DROP TABLE IF EXISTS spotify;
/*
CREATE TABLE IF NOT EXISTS spotify (
    id INT AUTO_INCREMENT PRIMARY KEY,
    access_Token VARCHAR(255) NOT NULL,
    expires_At TIMESTAMP NOT NULL  -- Add a column to store token expiration timestamp
);
*/

#DROP TABLE IF EXISTS playlist;
/*
CREATE TABLE playlist (
	
    collaborative BOOLEAN,
    description TEXT,
    playlist_href VARCHAR(255),
    playlist_name VARCHAR(255),
    snapshot_id VARCHAR(255),
    public BOOLEAN,
    type VARCHAR(255),
    uri VARCHAR(255),
    playlist_id VARCHAR(255) NOT NULL PRIMARY KEY
);

*/


select * FROM playlist;
#select * FROM spotify; -- Token





