CREATE TABLE IF NOT EXISTS klink (
    id uuid PRIMARY KEY,
    name text
        NOT NULL,
    description text,
    date timestamp DEFAULT now() 
        NOT NULL
);

CREATE TABLE IF NOT EXISTS klink_entry (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    klink_id uuid 
        REFERENCES klink(id) 
        ON DELETE CASCADE,
    value text
);  

CREATE TABLE IF NOT EXISTS klink_key (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    read_key varchar(8) NOT NULL,
    write_key varchar(8),
    klink_id uuid
        REFERENCES klink(id)
        ON DELETE CASCADE
);
