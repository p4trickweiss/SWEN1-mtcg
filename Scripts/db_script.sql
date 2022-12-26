DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
                        "uid" SERIAL,
                        "username" varchar(64) NOT NULL unique ,
                        "password" varchar(256) NOT NULL,
                        "token" varchar(64) DEFAULT NULL,
                        "name" varchar(64) DEFAULT NULL,
                        "bio" varchar(128) DEFAULT NULL,
                        "image" varchar(64) DEFAULT NULL,
                        "coins" integer NOT NULL DEFAULT 20,
                        "elo" integer NOT NULL DEFAULT 100,
                        "wins" integer NOT NULL DEFAULT 0,
                        "losses" integer NOT NULL DEFAULT 0,
                        PRIMARY KEY ("uid")
);

DROP TABLE IF EXISTS "package";
CREATE TABLE "package" (
                           "pid" SERIAL,
                           "price" INTEGER NOT NULL DEFAULT 5,
                           "is_available" BOOLEAN NOT NULL DEFAULT TRUE,
                           PRIMARY KEY ("pid")
);

DROP TABLE IF EXISTS "card";
CREATE TABLE "card" (
                        "cid" varchar(64) NOT NULL,
                        "name" varchar(64) NOT NULL,
                        "damage" integer NOT NULL,
                        "type" varchar(64),
                        "element" varchar(64),
                        "in_deck" BOOL DEFAULT FALSE,
                        "is_locked" BOOL DEFAULT FALSE,
                        "fk_pid" INTEGER,
                        "fk_uid" INTEGER,
                        PRIMARY KEY ("cid"),
                        CONSTRAINT fk_user
                            FOREIGN KEY ("fk_uid")
                                REFERENCES "user"("uid"),
                        CONSTRAINT fk_package
                            FOREIGN KEY ("fk_pid")
                                REFERENCES "package"("pid")
);

DROP TABLE IF EXISTS "store";
CREATE TABLE "store" (
                         "tid" varchar(64) NOT NULL,
                         "cardToTrade" varchar(64) NOT NULL,
                         "minimumDamage" integer NOT NULL,
                         "type" varchar(64) NOT NULL,
                         "fk_uid" integer NOT NULL,
                         PRIMARY KEY ("tid"),
                         CONSTRAINT fk_user
                             FOREIGN KEY ("fk_uid")
                                 REFERENCES "user"("uid")
);

DROP TABLE IF EXISTS "lobby";
CREATE TABLE "lobby" (
                         "bid" SERIAL,
                         "player" varchar(64) NOT NULL
);

DROP TABLE IF EXISTS "battle-log";
CREATE TABLE "battle-log" (
                              "bid" integer,
                              "playerA" varchar(64),
                              "playerB" varchar(64),
                              "cardPlayerA" varchar(64),
                              "cardPlayerB" varchar(64),
                              "damageCardA" integer,
                              "damageCardB" integer,
                              "finished" bool
);